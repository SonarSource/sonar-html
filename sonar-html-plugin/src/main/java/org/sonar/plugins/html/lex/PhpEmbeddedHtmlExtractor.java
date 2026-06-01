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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import org.sonar.plugins.html.node.Attribute;
import org.sonar.plugins.html.node.DirectiveNode;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;

/**
 * Extracts HTML nodes from PHP string literals embedded inside PHP directives
 * and splices them into the main node list so that all visitors receive the
 * full analysis lifecycle (startElement/characters/endElement/...) for the
 * embedded HTML, with accurate file-coordinate positions.
 */
final class PhpEmbeddedHtmlExtractor {

  private static final Pattern INTERPOLATION = Pattern.compile(
    "\\{\\$[^}]+\\}|<\\?=.*?\\?>|\\$\\{?[a-zA-Z_]\\w*\\}?");
  private static final Pattern EMBEDDED_HTML = Pattern.compile("<\\s*[/a-zA-Z]");
  private static final String DYNAMIC = "${dynamic}";
  // initial slack for embedded nodes per directive
  private static final int EXTRA_CAPACITY = 8;

  /**
   * Returns a new list with embedded HTML nodes spliced in after each PHP directive
   * that contains HTML in string literals. The directive node itself is preserved.
   */
  List<Node> expand(List<Node> nodes) {
    List<Node> result = new ArrayList<>(nodes.size() + EXTRA_CAPACITY);
    for (Node node : nodes) {
      result.add(node);
      if (node instanceof DirectiveNode directive && isPhpDirective(directive)) {
        for (StringLiteral literal : extractLiterals(directive)) {
          String sanitized = sanitizeInterpolations(literal.value());
          if (EMBEDDED_HTML.matcher(sanitized).find()) {
            List<Node> embedded = reLex(sanitized);
            rebasePositions(embedded, literal.lineOffset(), literal.columnOffset());
            result.addAll(embedded);
          }
        }
      }
    }
    return result;
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
        result.add(readHeredocOrNowdoc(code, c));
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
    if (nowdoc && c.pos < code.length() && code.charAt(c.pos) == '\'') { c.col++; c.pos++; }
    while (c.pos < code.length() && code.charAt(c.pos) != '\n') { c.col++; c.pos++; }
    if (c.pos < code.length()) { c.line++; c.col = 0; c.pos++; }
    return readHeredocBody(code, c, label);
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

  private static StringLiteral readHeredocBody(String code, Cursor c, String label) {
    int bodyLine = c.line, bodyCol = c.col;
    StringBuilder body = new StringBuilder();
    int len = code.length();
    while (c.pos < len) {
      int lineStart = c.pos, indentLen = 0;
      while (c.pos < len && (code.charAt(c.pos) == ' ' || code.charAt(c.pos) == '\t')) {
        c.pos++; indentLen++;
      }
      if (code.regionMatches(c.pos, label, 0, label.length()) && isHeredocEnd(code, c.pos + label.length(), len)) {
        if (indentLen > 0 && body.length() > 0) { body = stripHeredocIndent(body, indentLen); }
        while (c.pos < len && code.charAt(c.pos) != '\n') { c.col++; c.pos++; }
        if (c.pos < len) { c.line++; c.col = 0; c.pos++; }
        break;
      }
      c.pos = lineStart;
      while (c.pos < len && code.charAt(c.pos) != '\n') { body.append(code.charAt(c.pos)); c.col++; c.pos++; }
      if (c.pos < len) { body.append('\n'); c.line++; c.col = 0; c.pos++; }
    }
    return new StringLiteral(body.toString(), bodyLine, bodyCol);
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
    StringBuilder sb = new StringBuilder();
    int len = code.length();
    while (c.pos < len) {
      char cc = code.charAt(c.pos);
      if (cc == '\\' && c.pos + 1 < len) {
        appendDoubleEscape(code.charAt(c.pos + 1), cc, sb, c);
      } else if (cc == '"') {
        c.col++; c.pos++;
        break;
      } else {
        sb.append(cc);
        if (cc == '\n') { c.line++; c.col = 0; } else { c.col++; }
        c.pos++;
      }
    }
    return new StringLiteral(sb.toString(), contentLine, contentCol);
  }

  private static void appendDoubleEscape(char next, char backslash, StringBuilder sb, Cursor c) {
    switch (next) {
      case '"':  sb.append('"');  c.col += 2; c.pos += 2; break;
      case '\\': sb.append('\\'); c.col += 2; c.pos += 2; break;
      case 'n':  sb.append('\n'); c.col += 2; c.pos += 2; break;
      case 'r':  sb.append('\r'); c.col += 2; c.pos += 2; break;
      case 't':  sb.append('\t'); c.col += 2; c.pos += 2; break;
      case '$':  sb.append('$');  c.col += 2; c.pos += 2; break;
      default:   sb.append(backslash); c.col++; c.pos++; break;
    }
  }

  private static StringLiteral readSingleQuotedString(String code, Cursor c) {
    c.col++; c.pos++; // skip opening '
    int contentLine = c.line, contentCol = c.col;
    StringBuilder sb = new StringBuilder();
    int len = code.length();
    while (c.pos < len) {
      char cc = code.charAt(c.pos);
      if (cc == '\\' && c.pos + 1 < len) {
        char next = code.charAt(c.pos + 1);
        if (next == '\\' || next == '\'') {
          sb.append(next); c.col += 2; c.pos += 2;
        } else {
          sb.append(cc); c.col++; c.pos++;
        }
      } else if (cc == '\'') {
        c.col++; c.pos++;
        break;
      } else {
        sb.append(cc);
        if (cc == '\n') { c.line++; c.col = 0; } else { c.col++; }
        c.pos++;
      }
    }
    return new StringLiteral(sb.toString(), contentLine, contentCol);
  }

  static String sanitizeInterpolations(String value) {
    return INTERPOLATION.matcher(value).replaceAll("\\${dynamic}");
  }

  static List<Node> reLex(String sanitized) {
    return new PageLexer().parseWithoutHierarchy(sanitized);
  }

  /**
   * Rebases re-lexed node positions from local (1-based line within sanitized string)
   * to file-absolute coordinates.
   *
   * @param embedded   nodes produced by re-lexing the sanitized literal
   * @param baseLine   1-based file line of the literal's first content character
   * @param baseCol    0-based file column of the literal's first content character
   */
  static void rebasePositions(List<Node> embedded, int baseLine, int baseCol) {
    for (Node node : embedded) {
      int localStart = node.getStartLinePosition();
      int localEnd = node.getEndLinePosition();
      node.setStartLinePosition(baseLine + localStart - 1);
      node.setEndLinePosition(baseLine + localEnd - 1);
      if (localStart == 1) { node.setStartColumnPosition(baseCol + node.getStartColumnPosition()); }
      if (localEnd == 1) { node.setEndColumnPosition(baseCol + node.getEndColumnPosition()); }
      if (node instanceof TagNode tag) {
        for (Attribute attr : tag.getAttributes()) {
          if (attr.getLine() > 0) { attr.setLine(baseLine + attr.getLine() - 1); }
        }
      }
    }
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

  record StringLiteral(String value, int lineOffset, int columnOffset) {
  }
}
