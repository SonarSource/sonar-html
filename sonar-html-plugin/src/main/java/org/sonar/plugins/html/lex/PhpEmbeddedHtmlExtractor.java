/*
 * SonarQube HTML
 * Copyright (C) SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * You can redistribute and/or modify this program under the terms of
 * the Sonar Source-Available License Version 1, as published by SonarSource Sàrl.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package org.sonar.plugins.html.lex;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.sonar.plugins.html.node.Attribute;
import org.sonar.plugins.html.node.DirectiveNode;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;
import org.sonar.plugins.html.node.TextNode;

/**
 * Extracts HTML nodes from PHP string literals embedded inside PHP directives
 * and splices them into the main node list so that all visitors receive the
 * full analysis lifecycle (startElement/characters/endElement/...) for the
 * embedded HTML, with accurate file-coordinate positions.
 */
final class PhpEmbeddedHtmlExtractor {

  private static final Pattern INTERPOLATION = Pattern.compile(
    "\\{\\$[^}]+\\}|\\$\\{?[a-zA-Z_]\\w*\\}?");
  private static final Pattern EMBEDDED_HTML = Pattern.compile("<\\s*[/a-zA-Z]");
  private static final String DYNAMIC_PLACEHOLDER = "${dynamic}";
  // initial slack for embedded nodes per directive
  private static final int EXTRA_CAPACITY = 8;
  // shared sentinel: heredoc bodies have no per-character source-column map
  private static final int[][] EMPTY_COLUMNS = new int[0][];

  /**
   * Returns a new list with embedded HTML nodes spliced in after each PHP directive
   * that contains HTML in string literals. The directive node itself is preserved.
   *
   * @param nodes flat list produced by the channel-dispatcher tokenizers
   * @return a new list with the same nodes plus any embedded HTML spliced in
   *         right after each PHP directive that contained it
   */
  static List<Node> expand(List<Node> nodes) {
    List<Node> result = new ArrayList<>(nodes.size() + EXTRA_CAPACITY);
    for (Node node : nodes) {
      result.add(node);
      if (node instanceof DirectiveNode directive && isPhpDirective(directive)) {
        spliceEmbedded(directive, result);
      }
    }
    return result;
  }

  private static void spliceEmbedded(DirectiveNode directive, List<Node> result) {
    List<Node> directiveEmbedded = new ArrayList<>();
    String code = directive.getCode();
    // Pull every PHP string literal out of the directive.
    List<StringLiteral> literals = extractLiterals(directive);
    int prevHtmlIdx = -1;
    for (int i = 0; i < literals.size(); i++) {
      StringLiteral literal = literals.get(i);
      String sanitized = sanitizeIfInterpolated(literal);
      // Skip literals that carry no HTML.
      if (!EMBEDDED_HTML.matcher(sanitized).find()) {
        continue;
      }
      // Bridge the gap to the previous HTML literal in this directive.
      if (prevHtmlIdx >= 0) {
        bridgeGap(directive, code, literals, prevHtmlIdx, i, directiveEmbedded);
      }
      // Re-lex as HTML, rebase positions to file coordinates.
      List<Node> embedded = reLex(sanitized);
      rebasePositions(embedded, literal);
      directiveEmbedded.addAll(embedded);
      prevHtmlIdx = i;
    }
    // Close any tag left open across the directive's literals.
    balanceUnclosedTags(directiveEmbedded);
    // Splice the embedded HTML right after the directive node.
    result.addAll(directiveEmbedded);
  }

  private static String sanitizeIfInterpolated(StringLiteral literal) {
    return literal.interpolated()
      ? sanitizeInterpolations(literal.value())
      : literal.value();
  }

  private static void bridgeGap(DirectiveNode directive, String code, List<StringLiteral> literals,
                                int prevHtmlIdx, int currentIdx, List<Node> sink) {
    StringLiteral prev = literals.get(prevHtmlIdx);
    StringLiteral current = literals.get(currentIdx);
    List<StringLiteral> intermediates = literals.subList(prevHtmlIdx + 1, currentIdx);
    if (isPureConcatenation(code, prev.rawEnd(), current.rawStart(), intermediates)) {
      // Pure concat: surface skipped literals as real text.
      for (StringLiteral inter : intermediates) {
        if (!inter.value().isEmpty()) {
          sink.add(literalTextNode(inter));
        }
      }
    } else {
      // Runtime expression: opaque non-blank placeholder.
      sink.add(dynamicGapText(directive));
    }
  }

  /**
   * Returns {@code true} when the raw directive code from {@code from} to
   * {@code to} contains nothing that could change runtime output beyond pure
   * string concatenation. Allowed tokens are whitespace, the PHP concatenation
   * operator {@code .}, grouping parentheses, line and block comments, and the
   * source spans of {@code intermediates} (literals that were extracted but
   * carry no HTML and are therefore subtracted from the gap).
   *
   * @param code          the directive's raw code
   * @param from          inclusive position to start scanning at
   * @param to            exclusive position to stop scanning at
   * @param intermediates literals fully contained inside {@code [from, to)};
   *                      their source spans are skipped during the scan
   * @return {@code true} iff no PHP expression sits between the two anchoring
   *         HTML-bearing literals
   */
  private static boolean isPureConcatenation(String code, int from, int to, List<StringLiteral> intermediates) {
    int idx = 0;
    int pos = from;
    while (pos < to) {
      if (idx < intermediates.size() && pos == intermediates.get(idx).rawStart()) {
        pos = intermediates.get(idx).rawEnd();
        idx++;
      } else {
        int advanced = advancePastTrivialToken(code, pos, to);
        if (advanced < 0) {
          return false;
        }
        pos = advanced;
      }
    }
    return true;
  }

  /**
   * Advances past one whitespace/concat/comment token starting at {@code pos}.
   * Returns the new position, or {@code -1} when {@code code.charAt(pos)} is
   * not a token the pure-concatenation scan allows.
   */
  private static int advancePastTrivialToken(String code, int pos, int to) {
    char ch = code.charAt(pos);
    if (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r' || ch == '.' || ch == '(' || ch == ')') {
      return pos + 1;
    }
    if (ch == '#' || (ch == '/' && pos + 1 < to && code.charAt(pos + 1) == '/')) {
      int p = pos;
      while (p < to && code.charAt(p) != '\n') {
        p++;
      }
      return p;
    }
    if (ch == '/' && pos + 1 < to && code.charAt(pos + 1) == '*') {
      int p = pos + 2;
      while (p + 1 < to && !(code.charAt(p) == '*' && code.charAt(p + 1) == '/')) {
        p++;
      }
      return Math.min(to, p + 2);
    }
    return -1;
  }

  static boolean isPhpDirective(DirectiveNode node) {
    String name = node.getNodeName();
    if (name == null) {
      return false;
    }
    name = name.toLowerCase(Locale.ROOT);
    return name.startsWith("?php") || name.equals("?=");
  }

  /**
   * Scans the directive's raw code and extracts every string literal with its
   * file-coordinate origin (line/column of the first content character).
   *
   * <p>Handles double-quoted strings, single-quoted strings, heredoc, nowdoc,
   * line comments ({@code //}, {@code #}), and block comments ({@code /* }).
   *
   * @param node the PHP directive whose raw code is scanned
   * @return the string literals found in {@code node.getCode()}, in source order,
   *         each carrying its decoded value and the source coordinates of its
   *         first content character
   */
  static List<StringLiteral> extractLiterals(DirectiveNode node) {
    List<StringLiteral> result = new ArrayList<>();
    String code = node.getCode();
    Cursor c = new Cursor(0, node.getStartLinePosition(), node.getStartColumnPosition());
    while (c.pos < code.length()) {
      StringLiteral literal = readNextLiteral(code, c);
      if (literal != null) {
        result.add(literal);
      }
    }
    return result;
  }

  /**
   * Advances {@code c} past whitespace, comments, and any non-literal token,
   * returning the next extracted {@link StringLiteral} or {@code null} when the
   * cursor stops at a position that does not start a literal (e.g. EOF).
   */
  private static StringLiteral readNextLiteral(String code, Cursor c) {
    int len = code.length();
    while (c.pos < len) {
      char ch = code.charAt(c.pos);
      if (ch == '\n') {
        c.line++;
        c.col = 0;
        c.pos++;
      } else if (ch == '#' || (ch == '/' && c.pos + 1 < len && code.charAt(c.pos + 1) == '/')) {
        skipLineComment(code, c);
      } else if (ch == '/' && c.pos + 1 < len && code.charAt(c.pos + 1) == '*') {
        skipBlockComment(code, c);
      } else if (ch == '<' && c.pos + 2 < len && code.charAt(c.pos + 1) == '<' && code.charAt(c.pos + 2) == '<') {
        int rawStart = c.pos;
        return readHeredocOrNowdoc(code, c, rawStart);
      } else if (ch == '"') {
        int rawStart = c.pos;
        return readDoubleQuotedString(code, c, rawStart);
      } else if (ch == '\'') {
        int rawStart = c.pos;
        return readSingleQuotedString(code, c, rawStart);
      } else {
        c.col++;
        c.pos++;
      }
    }
    return null;
  }

  private static void skipLineComment(String code, Cursor c) {
    while (c.pos < code.length() && code.charAt(c.pos) != '\n') {
      c.col++;
      c.pos++;
    }
  }

  private static void skipBlockComment(String code, Cursor c) {
    c.col += 2;
    c.pos += 2;
    int len = code.length();
    while (c.pos + 1 < len && !(code.charAt(c.pos) == '*' && code.charAt(c.pos + 1) == '/')) {
      if (code.charAt(c.pos) == '\n') {
        c.line++;
        c.col = 0;
      } else {
        c.col++;
      }
      c.pos++;
    }
    if (c.pos + 1 < len) {
      c.col += 2;
      c.pos += 2;
    }
  }

  private static StringLiteral readHeredocOrNowdoc(String code, Cursor c, int rawStart) {
    // skip <<<
    c.col += 3;
    c.pos += 3;
    skipSpaces(code, c);
    boolean nowdoc = c.pos < code.length() && code.charAt(c.pos) == '\'';
    if (nowdoc) {
      c.col++;
      c.pos++;
    }
    String label = readHeredocLabel(code, c);
    if (label.isEmpty()) {
      return null;
    }
    if (nowdoc && c.pos < code.length() && code.charAt(c.pos) == '\'') {
      c.col++;
      c.pos++;
    }
    skipUntilNewline(code, c);
    consumeNewline(code, c);
    return readHeredocBody(code, c, label, !nowdoc, rawStart);
  }

  private static void skipSpaces(String code, Cursor c) {
    while (c.pos < code.length() && code.charAt(c.pos) == ' ') {
      c.col++;
      c.pos++;
    }
  }

  private static void skipUntilNewline(String code, Cursor c) {
    while (c.pos < code.length() && code.charAt(c.pos) != '\n') {
      c.col++;
      c.pos++;
    }
  }

  private static void consumeNewline(String code, Cursor c) {
    if (c.pos < code.length()) {
      c.line++;
      c.col = 0;
      c.pos++;
    }
  }

  private static String readHeredocLabel(String code, Cursor c) {
    StringBuilder label = new StringBuilder();
    while (c.pos < code.length() && code.charAt(c.pos) != '\'' && code.charAt(c.pos) != '"'
      && code.charAt(c.pos) != '\n' && !Character.isWhitespace(code.charAt(c.pos))) {
      label.append(code.charAt(c.pos));
      c.col++;
      c.pos++;
    }
    return label.toString();
  }

  /**
   * Returns {@code null} when EOF is reached before the closing label is seen
   * (malformed heredoc / typo in terminator); the caller drops the literal so
   * downstream re-lexing does not swallow the rest of the directive as HTML.
   *
   * @param code         the directive's raw code
   * @param c            cursor positioned at the first body character (just past
   *                     the {@code <<<LABEL\n} header)
   * @param label        the closing label that terminates the body
   * @param interpolated {@code true} for heredoc (variable interpolation
   *                     applies), {@code false} for nowdoc (literal body)
   * @param rawStart     position of the opening {@code <<<} in the directive's
   *                     raw code, used to anchor the gap-analysis pass that
   *                     decides whether a dynamic placeholder is needed
   *                     between two HTML-bearing literals
   * @return the heredoc/nowdoc body literal, or {@code null} if EOF is reached
   *         before the closing label is seen
   */
  private static StringLiteral readHeredocBody(String code, Cursor c, String label, boolean interpolated, int rawStart) {
    int bodyLine = c.line;
    int bodyCol = c.col;
    StringBuilder body = new StringBuilder();
    int len = code.length();
    boolean terminatorFound = false;
    while (c.pos < len) {
      int lineStart = c.pos;
      int indentLen = measureLineIndent(code, c, len);
      if (isHeredocTerminatorLine(code, c, label, len)) {
        if (indentLen > 0 && !body.isEmpty()) {
          body = stripHeredocIndent(body, indentLen);
        }
        skipUntilNewline(code, c);
        consumeNewline(code, c);
        terminatorFound = true;
        break;
      }
      c.pos = lineStart;
      copyBodyLine(code, c, body, len);
    }
    if (!terminatorFound) {
      return null;
    }
    return new StringLiteral(body.toString(), bodyLine, bodyCol, interpolated, EMPTY_COLUMNS, rawStart, c.pos);
  }

  private static int measureLineIndent(String code, Cursor c, int len) {
    int indentLen = 0;
    while (c.pos < len && (code.charAt(c.pos) == ' ' || code.charAt(c.pos) == '\t')) {
      c.pos++;
      indentLen++;
    }
    return indentLen;
  }

  private static boolean isHeredocTerminatorLine(String code, Cursor c, String label, int len) {
    return code.regionMatches(c.pos, label, 0, label.length())
      && isHeredocEnd(code, c.pos + label.length(), len);
  }

  private static void copyBodyLine(String code, Cursor c, StringBuilder body, int len) {
    while (c.pos < len && code.charAt(c.pos) != '\n') {
      body.append(code.charAt(c.pos));
      c.col++;
      c.pos++;
    }
    if (c.pos < len) {
      body.append('\n');
      c.line++;
      c.col = 0;
      c.pos++;
    }
  }

  private static boolean isHeredocEnd(String code, int afterLabel, int len) {
    return afterLabel >= len || code.charAt(afterLabel) == ';'
      || code.charAt(afterLabel) == '\n' || code.charAt(afterLabel) == '\r';
  }

  private static StringBuilder stripHeredocIndent(StringBuilder body, int indentLen) {
    String[] lines = body.toString().split("\n", -1);
    StringBuilder stripped = new StringBuilder(body.length());
    for (int li = 0; li < lines.length; li++) {
      String line = lines[li];
      stripped.append(line.length() >= indentLen ? line.substring(indentLen) : line);
      if (li < lines.length - 1) {
        stripped.append('\n');
      }
    }
    return stripped;
  }

  private static StringLiteral readDoubleQuotedString(String code, Cursor c, int rawStart) {
    // skip opening "
    c.col++;
    c.pos++;
    int contentLine = c.line;
    int contentCol = c.col;
    LiteralBuilder builder = new LiteralBuilder();
    int len = code.length();
    while (c.pos < len) {
      char cc = code.charAt(c.pos);
      if (cc == '\\' && c.pos + 1 < len) {
        appendDoubleEscape(code.charAt(c.pos + 1), cc, builder, c);
      } else if (cc == '"') {
        c.col++;
        c.pos++;
        break;
      } else {
        appendLiteralChar(builder, c, cc);
      }
    }
    return new StringLiteral(builder.text(), contentLine, contentCol, true, builder.sourceColumns(), rawStart, c.pos);
  }

  private static void appendLiteralChar(LiteralBuilder builder, Cursor c, char cc) {
    builder.append(cc, c.col);
    if (cc == '\n') {
      c.line++;
      c.col = 0;
    } else {
      c.col++;
    }
    c.pos++;
  }

  /**
   * Decodes a PHP double-quoted escape sequence. {@code \n}, {@code \r} and
   * {@code \t} are emitted as a single space rather than the corresponding
   * control character so that the re-lex line counter is not advanced past the
   * literal's true source line.
   *
   * @param next      the character following the leading backslash
   * @param backslash the leading backslash itself, written verbatim for unknown
   *                  escape sequences
   * @param builder   accumulator for the decoded literal value and its per-char
   *                  source-column map
   * @param c         cursor positioned at the backslash; advanced past the
   *                  escape sequence (one or two source chars) on return
   */
  private static void appendDoubleEscape(char next, char backslash, LiteralBuilder builder, Cursor c) {
    int srcCol = c.col;
    char decoded;
    switch (next) {
      case '"':
        decoded = '"';
        break;
      case '\\':
        decoded = '\\';
        break;
      case '$':
        decoded = '$';
        break;
      case 'n', 'r', 't':
        decoded = ' ';
        break;
      default:
        builder.append(backslash, srcCol);
        c.col++;
        c.pos++;
        return;
    }
    builder.append(decoded, srcCol);
    c.col += 2;
    c.pos += 2;
  }

  private static StringLiteral readSingleQuotedString(String code, Cursor c, int rawStart) {
    // skip opening '
    c.col++;
    c.pos++;
    int contentLine = c.line;
    int contentCol = c.col;
    LiteralBuilder builder = new LiteralBuilder();
    int len = code.length();
    while (c.pos < len) {
      char cc = code.charAt(c.pos);
      if (cc == '\\' && c.pos + 1 < len) {
        appendSingleEscape(code.charAt(c.pos + 1), cc, builder, c);
      } else if (cc == '\'') {
        c.col++;
        c.pos++;
        break;
      } else {
        appendLiteralChar(builder, c, cc);
      }
    }
    // Single-quoted strings do not interpolate variables in PHP, so the
    // sanitisation pass that rewrites $var / {$expr} into a dynamic marker is
    // skipped to avoid silently hiding real attribute values.
    return new StringLiteral(builder.text(), contentLine, contentCol, false, builder.sourceColumns(), rawStart, c.pos);
  }

  private static void appendSingleEscape(char next, char backslash, LiteralBuilder builder, Cursor c) {
    if (next == '\\' || next == '\'') {
      builder.append(next, c.col);
      c.col += 2;
      c.pos += 2;
    } else {
      builder.append(backslash, c.col);
      c.col++;
      c.pos++;
    }
  }

  static String sanitizeInterpolations(String value) {
    return INTERPOLATION.matcher(value).replaceAll(Matcher.quoteReplacement(DYNAMIC_PLACEHOLDER));
  }

  static List<Node> reLex(String sanitized) {
    return new PageLexer().parseWithoutHierarchy(sanitized);
  }

  /**
   * Rebases re-lexed node positions from local (1-based line within sanitized string)
   * to file-absolute coordinates using the literal's per-character source-column map
   * when available, otherwise falling back to a flat offset.
   *
   * @param embedded nodes produced by re-lexing the sanitized literal; their
   *                 line/column positions are mutated in place
   * @param literal  the originating literal, providing the file-coordinate base
   *                 and the per-character source-column map
   */
  static void rebasePositions(List<Node> embedded, StringLiteral literal) {
    int baseLine = literal.lineOffset();
    int baseCol = literal.columnOffset();
    int[][] map = literal.sourceColumns();
    for (Node node : embedded) {
      int localStart = node.getStartLinePosition();
      int localEnd = node.getEndLinePosition();
      node.setStartLinePosition(baseLine + localStart - 1);
      node.setEndLinePosition(baseLine + localEnd - 1);
      node.setStartColumnPosition(sourceCol(map, localStart, node.getStartColumnPosition(), baseCol));
      node.setEndColumnPosition(sourceCol(map, localEnd, node.getEndColumnPosition(), baseCol));
      if (node instanceof TagNode tag) {
        for (Attribute attr : tag.getAttributes()) {
          if (attr.getLine() > 0) {
            attr.setLine(baseLine + attr.getLine() - 1);
          }
        }
      }
    }
  }

  private static int sourceCol(int[][] map, int localLine, int localCol, int baseCol) {
    int lineIdx = localLine - 1;
    if (lineIdx >= 0 && lineIdx < map.length) {
      int[] lineCols = map[lineIdx];
      if (localCol >= 0 && localCol < lineCols.length) {
        return lineCols[localCol];
      }
      if (lineCols.length > 0) {
        return lineCols[lineCols.length - 1] + 1;
      }
    }
    return (localLine == 1) ? (baseCol + localCol) : localCol;
  }

  /**
   * Appends a synthetic {@code </tag>} close for every tag that the re-lexed
   * fragment leaves open, so that an open-only literal (e.g. {@code echo
   * "<span>";}) does not turn every subsequent real-file tag into a child of
   * the synthetic node when {@code createNodeHierarchy} runs.
   *
   * @param embedded nodes produced by re-lexing one literal; mutated in place
   *                 by appending synthetic end tags for any element left open
   */
  private static void balanceUnclosedTags(List<Node> embedded) {
    Deque<TagNode> openStack = new ArrayDeque<>();
    for (Node node : embedded) {
      if (node instanceof TagNode tag && !tag.hasEnd()) {
        updateOpenStack(openStack, tag);
      }
    }
    while (!openStack.isEmpty()) {
      TagNode open = openStack.pop();
      embedded.add(syntheticEndTag(open));
    }
  }

  private static void updateOpenStack(Deque<TagNode> openStack, TagNode tag) {
    if (tag.isEndElement()) {
      if (!openStack.isEmpty() && openStack.peek().equalsElementName(tag.getNodeName())) {
        openStack.pop();
      }
    } else if (!isVoidElement(tag)) {
      // Void elements (br, img, input, ...) carry no content and cannot have
      // a matching close tag, so they must not contribute to the open stack.
      openStack.push(tag);
    }
  }

  private static boolean isVoidElement(TagNode tag) {
    return PageLexer.VOID_ELEMENTS.contains(tag.getNodeName().toLowerCase(Locale.ROOT));
  }

  private static TagNode syntheticEndTag(TagNode open) {
    TagNode end = new TagNode();
    end.setNodeName(open.getNodeName());
    end.setCode("</" + open.getNodeName() + ">");
    end.setStartLinePosition(open.getEndLinePosition());
    end.setStartColumnPosition(open.getEndColumnPosition());
    end.setEndLinePosition(open.getEndLinePosition());
    end.setEndColumnPosition(open.getEndColumnPosition());
    return end;
  }

  private static TextNode dynamicGapText(DirectiveNode directive) {
    TextNode text = new TextNode();
    text.setCode(DYNAMIC_PLACEHOLDER);
    text.setStartLinePosition(directive.getStartLinePosition());
    text.setStartColumnPosition(directive.getStartColumnPosition());
    text.setEndLinePosition(directive.getEndLinePosition());
    text.setEndColumnPosition(directive.getEndColumnPosition());
    return text;
  }

  private static TextNode literalTextNode(StringLiteral literal) {
    TextNode text = new TextNode();
    String value = literal.value();
    text.setCode(value);
    text.setStartLinePosition(literal.lineOffset());
    text.setStartColumnPosition(literal.columnOffset());
    // Count real newlines so a multi-line literal lands on the right end line.
    int extraLines = 0;
    int lastLineStart = 0;
    for (int i = 0; i < value.length(); i++) {
      if (value.charAt(i) == '\n') {
        extraLines++;
        lastLineStart = i + 1;
      }
    }
    text.setEndLinePosition(literal.lineOffset() + extraLines);
    text.setEndColumnPosition((extraLines == 0)
      ? (literal.columnOffset() + value.length())
      : (value.length() - lastLineStart));
    return text;
  }

  private static final class Cursor {
    int pos;
    int line;
    int col;

    Cursor(int pos, int line, int col) {
      this.pos = pos;
      this.line = line;
      this.col = col;
    }
  }

  /**
   * Accumulates the decoded literal value while recording, for each appended
   * character, the source column it originated from. The resulting {@code
   * int[][]} maps local {@code (line, col)} pairs (line 1-based, col 0-based)
   * back to source columns so escape sequences ({@code \"} → {@code "}) don't
   * shift downstream token coordinates.
   */
  private static final class LiteralBuilder {
    private final StringBuilder sb = new StringBuilder();
    private final List<int[]> columnsPerLine = new ArrayList<>();
    private int[] currentLine = new int[32];
    private int currentLen;

    void append(char c, int srcCol) {
      if (currentLen >= currentLine.length) {
        currentLine = Arrays.copyOf(currentLine, currentLine.length * 2);
      }
      currentLine[currentLen] = srcCol;
      currentLen++;
      sb.append(c);
      if (c == '\n') {
        columnsPerLine.add(Arrays.copyOf(currentLine, currentLen));
        currentLen = 0;
      }
    }

    String text() {
      return sb.toString();
    }

    int[][] sourceColumns() {
      List<int[]> all = new ArrayList<>(columnsPerLine);
      all.add(Arrays.copyOf(currentLine, currentLen));
      return all.toArray(new int[0][]);
    }
  }

  record StringLiteral(String value, int lineOffset, int columnOffset, boolean interpolated, int[][] sourceColumns,
                       int rawStart, int rawEnd) {

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof StringLiteral other)) {
        return false;
      }
      return lineOffset == other.lineOffset
        && columnOffset == other.columnOffset
        && interpolated == other.interpolated
        && rawStart == other.rawStart
        && rawEnd == other.rawEnd
        && java.util.Objects.equals(value, other.value)
        && Arrays.deepEquals(sourceColumns, other.sourceColumns);
    }

    @Override
    public int hashCode() {
      return java.util.Objects.hash(value, lineOffset, columnOffset, interpolated, rawStart, rawEnd)
        ^ Arrays.deepHashCode(sourceColumns);
    }

    @Override
    public String toString() {
      return "StringLiteral[value=" + value
        + ", lineOffset=" + lineOffset
        + ", columnOffset=" + columnOffset
        + ", interpolated=" + interpolated
        + ", sourceColumns=" + Arrays.deepToString(sourceColumns)
        + ", rawStart=" + rawStart
        + ", rawEnd=" + rawEnd + ']';
    }
  }
}
