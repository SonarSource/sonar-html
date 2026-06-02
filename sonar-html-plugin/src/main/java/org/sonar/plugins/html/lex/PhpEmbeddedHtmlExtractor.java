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
import org.sonar.plugins.html.node.NodeType;
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

  /**
   * Returns a new list with embedded HTML nodes spliced in after each PHP directive
   * that contains HTML in string literals. The directive node itself is preserved.
   *
   * @param nodes flat list produced by the channel-dispatcher tokenizers
   * @return a new list with the same nodes plus any embedded HTML spliced in
   *         right after each PHP directive that contained it
   */
  List<Node> expand(List<Node> nodes) {
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
    boolean previousSpliced = false;
    for (StringLiteral literal : extractLiterals(directive)) {
      String sanitized = literal.interpolated()
        ? sanitizeInterpolations(literal.value())
        : literal.value();
      if (!EMBEDDED_HTML.matcher(sanitized).find()) {
        continue;
      }
      if (previousSpliced) {
        // Gap between two HTML-bearing literals from the same directive stands
        // for any non-literal PHP expression that produced runtime content;
        // record a synthetic dynamic-marker text node so that content-sensitive
        // checks (e.g. AnchorsHaveContentCheck) see non-blank content.
        directiveEmbedded.add(dynamicGapText(directive));
      }
      List<Node> embedded = reLex(sanitized);
      rebasePositions(embedded, literal);
      directiveEmbedded.addAll(embedded);
      previousSpliced = true;
    }
    // Balance across all literals in the directive, not per literal, so a tag
    // opened in one literal and closed in another (with a dynamic gap between)
    // does not get a synthetic close inserted between them.
    balanceUnclosedTags(directiveEmbedded);
    result.addAll(directiveEmbedded);
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
    int len = code.length();
    while (c.pos < len) {
      char ch = code.charAt(c.pos);
      if (ch == '\n') {
        c.line++; c.col = 0; c.pos++;
      } else if (ch == '#' || (ch == '/' && c.pos + 1 < len && code.charAt(c.pos + 1) == '/')) {
        skipLineComment(code, c);
      } else if (ch == '/' && c.pos + 1 < len && code.charAt(c.pos + 1) == '*') {
        skipBlockComment(code, c);
      } else if (ch == '<' && c.pos + 2 < len && code.charAt(c.pos + 1) == '<' && code.charAt(c.pos + 2) == '<') {
        StringLiteral literal = readHeredocOrNowdoc(code, c);
        if (literal != null) {
          result.add(literal);
        }
      } else if (ch == '"') {
        result.add(readDoubleQuotedString(code, c));
      } else if (ch == '\'') {
        result.add(readSingleQuotedString(code, c));
      } else {
        c.col++; c.pos++;
      }
    }
    return result;
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
        c.line++; c.col = 0;
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

  private static StringLiteral readHeredocOrNowdoc(String code, Cursor c) {
    c.col += 3; c.pos += 3; // skip <<<
    while (c.pos < code.length() && code.charAt(c.pos) == ' ') { c.col++; c.pos++; }
    boolean nowdoc = c.pos < code.length() && code.charAt(c.pos) == '\'';
    if (nowdoc) { c.col++; c.pos++; }
    String label = readHeredocLabel(code, c);
    if (label.isEmpty()) {
      return null;
    }
    if (nowdoc && c.pos < code.length() && code.charAt(c.pos) == '\'') { c.col++; c.pos++; }
    while (c.pos < code.length() && code.charAt(c.pos) != '\n') { c.col++; c.pos++; }
    if (c.pos < code.length()) { c.line++; c.col = 0; c.pos++; }
    return readHeredocBody(code, c, label, !nowdoc);
  }

  private static String readHeredocLabel(String code, Cursor c) {
    StringBuilder label = new StringBuilder();
    while (c.pos < code.length() && code.charAt(c.pos) != '\'' && code.charAt(c.pos) != '"'
      && code.charAt(c.pos) != '\n' && !Character.isWhitespace(code.charAt(c.pos))) {
      label.append(code.charAt(c.pos));
      c.col++; c.pos++;
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
   * @return the heredoc/nowdoc body literal, or {@code null} if EOF is reached
   *         before the closing label is seen
   */
  private static StringLiteral readHeredocBody(String code, Cursor c, String label, boolean interpolated) {
    int bodyLine = c.line, bodyCol = c.col;
    StringBuilder body = new StringBuilder();
    int len = code.length();
    boolean terminatorFound = false;
    while (c.pos < len) {
      int lineStart = c.pos, indentLen = 0;
      while (c.pos < len && (code.charAt(c.pos) == ' ' || code.charAt(c.pos) == '\t')) {
        c.pos++; indentLen++;
      }
      if (code.regionMatches(c.pos, label, 0, label.length()) && isHeredocEnd(code, c.pos + label.length(), len)) {
        if (indentLen > 0 && body.length() > 0) { body = stripHeredocIndent(body, indentLen); }
        while (c.pos < len && code.charAt(c.pos) != '\n') { c.col++; c.pos++; }
        if (c.pos < len) { c.line++; c.col = 0; c.pos++; }
        terminatorFound = true;
        break;
      }
      c.pos = lineStart;
      while (c.pos < len && code.charAt(c.pos) != '\n') { body.append(code.charAt(c.pos)); c.col++; c.pos++; }
      if (c.pos < len) { body.append('\n'); c.line++; c.col = 0; c.pos++; }
    }
    if (!terminatorFound) {
      return null;
    }
    return new StringLiteral(body.toString(), bodyLine, bodyCol, interpolated, null);
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
      if (li < lines.length - 1) { stripped.append('\n'); }
    }
    return stripped;
  }

  private static StringLiteral readDoubleQuotedString(String code, Cursor c) {
    c.col++; c.pos++; // skip opening "
    int contentLine = c.line, contentCol = c.col;
    LiteralBuilder builder = new LiteralBuilder();
    int len = code.length();
    while (c.pos < len) {
      char cc = code.charAt(c.pos);
      if (cc == '\\' && c.pos + 1 < len) {
        appendDoubleEscape(code.charAt(c.pos + 1), cc, builder, c);
      } else if (cc == '"') {
        c.col++; c.pos++;
        break;
      } else {
        builder.append(cc, c.col);
        if (cc == '\n') { c.line++; c.col = 0; } else { c.col++; }
        c.pos++;
      }
    }
    return new StringLiteral(builder.text(), contentLine, contentCol, true, builder.sourceColumns());
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
    switch (next) {
      case '"':  builder.append('"',  srcCol); c.col += 2; c.pos += 2; break;
      case '\\': builder.append('\\', srcCol); c.col += 2; c.pos += 2; break;
      case 'n':  builder.append(' ',  srcCol); c.col += 2; c.pos += 2; break;
      case 'r':  builder.append(' ',  srcCol); c.col += 2; c.pos += 2; break;
      case 't':  builder.append(' ',  srcCol); c.col += 2; c.pos += 2; break;
      case '$':  builder.append('$',  srcCol); c.col += 2; c.pos += 2; break;
      default:   builder.append(backslash, srcCol); c.col++; c.pos++; break;
    }
  }

  private static StringLiteral readSingleQuotedString(String code, Cursor c) {
    c.col++; c.pos++; // skip opening '
    int contentLine = c.line, contentCol = c.col;
    LiteralBuilder builder = new LiteralBuilder();
    int len = code.length();
    while (c.pos < len) {
      char cc = code.charAt(c.pos);
      if (cc == '\\' && c.pos + 1 < len) {
        char next = code.charAt(c.pos + 1);
        if (next == '\\' || next == '\'') {
          builder.append(next, c.col); c.col += 2; c.pos += 2;
        } else {
          builder.append(cc, c.col); c.col++; c.pos++;
        }
      } else if (cc == '\'') {
        c.col++; c.pos++;
        break;
      } else {
        builder.append(cc, c.col);
        if (cc == '\n') { c.line++; c.col = 0; } else { c.col++; }
        c.pos++;
      }
    }
    // Single-quoted strings do not interpolate variables in PHP, so the
    // sanitisation pass that rewrites $var / {$expr} into a dynamic marker is
    // skipped to avoid silently hiding real attribute values.
    return new StringLiteral(builder.text(), contentLine, contentCol, false, builder.sourceColumns());
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
          if (attr.getLine() > 0) { attr.setLine(baseLine + attr.getLine() - 1); }
        }
      }
    }
  }

  private static int sourceCol(int[][] map, int localLine, int localCol, int baseCol) {
    if (map != null && localLine - 1 >= 0 && localLine - 1 < map.length) {
      int[] lineCols = map[localLine - 1];
      if (localCol >= 0 && localCol < lineCols.length) {
        return lineCols[localCol];
      }
      if (lineCols.length > 0) {
        return lineCols[lineCols.length - 1] + 1;
      }
    }
    return localLine == 1 ? baseCol + localCol : localCol;
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
      if (node.getNodeType() != NodeType.TAG) {
        continue;
      }
      TagNode tag = (TagNode) node;
      if (tag.hasEnd()) {
        continue;
      }
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
    while (!openStack.isEmpty()) {
      TagNode open = openStack.pop();
      embedded.add(syntheticEndTag(open));
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
      currentLine[currentLen++] = srcCol;
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

  record StringLiteral(String value, int lineOffset, int columnOffset, boolean interpolated, int[][] sourceColumns) {
  }
}
