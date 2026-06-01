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

  /**
   * Returns a new list with embedded HTML nodes spliced in after each PHP directive
   * that contains HTML in string literals. The directive node itself is preserved.
   */
  List<Node> expand(List<Node> nodes) {
    List<Node> result = new ArrayList<>(nodes.size() + 8);
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
   * <p>Handles:
   * <ul>
   *   <li>Double-quoted strings with standard PHP escape sequences</li>
   *   <li>Single-quoted strings (only {@code \\} and {@code \'} are escapes)</li>
   *   <li>Heredoc {@code <<<LABEL ... LABEL;} with interpolation</li>
   *   <li>Nowdoc {@code <<<'LABEL' ... LABEL;} without interpolation</li>
   *   <li>Line comments {@code //} and {@code #} (skipped)</li>
   *   <li>Block comments {@code /* ... * /} (skipped)</li>
   * </ul>
   */
  static List<StringLiteral> extractLiterals(DirectiveNode node) {
    List<StringLiteral> result = new ArrayList<>();
    String code = node.getCode();
    int len = code.length();

    int curLine = node.getStartLinePosition();
    int curCol = node.getStartColumnPosition();

    int i = 0;
    while (i < len) {
      char c = code.charAt(i);

      if (c == '\n') {
        curLine++;
        curCol = 0;
        i++;
        continue;
      }

      // Line comment: // or #
      if (c == '#' || (c == '/' && i + 1 < len && code.charAt(i + 1) == '/')) {
        while (i < len && code.charAt(i) != '\n') {
          curCol++;
          i++;
        }
        continue;
      }

      // Block comment: /* ... */
      if (c == '/' && i + 1 < len && code.charAt(i + 1) == '*') {
        curCol += 2;
        i += 2;
        while (i + 1 < len && !(code.charAt(i) == '*' && code.charAt(i + 1) == '/')) {
          if (code.charAt(i) == '\n') {
            curLine++;
            curCol = 0;
          } else {
            curCol++;
          }
          i++;
        }
        if (i + 1 < len) {
          curCol += 2;
          i += 2; // consume */
        }
        continue;
      }

      // Heredoc or Nowdoc: <<<
      if (c == '<' && i + 2 < len && code.charAt(i + 1) == '<' && code.charAt(i + 2) == '<') {
        curCol += 3;
        i += 3;
        // Skip optional spaces
        while (i < len && code.charAt(i) == ' ') {
          curCol++;
          i++;
        }
        // Nowdoc has a single-quoted label
        boolean nowdoc = i < len && code.charAt(i) == '\'';
        if (nowdoc) {
          curCol++;
          i++;
        }
        // Read the label identifier
        StringBuilder label = new StringBuilder();
        while (i < len && code.charAt(i) != '\'' && code.charAt(i) != '"'
          && code.charAt(i) != '\n' && !Character.isWhitespace(code.charAt(i))) {
          label.append(code.charAt(i));
          curCol++;
          i++;
        }
        if (nowdoc && i < len && code.charAt(i) == '\'') {
          curCol++;
          i++; // closing quote of nowdoc label
        }
        // Skip to end of the opening line
        while (i < len && code.charAt(i) != '\n') {
          curCol++;
          i++;
        }
        if (i < len) {
          curLine++;
          curCol = 0;
          i++; // consume the newline
        }
        // Now at the start of the heredoc/nowdoc body
        int bodyLine = curLine;
        int bodyCol = curCol;
        String labelStr = label.toString();
        StringBuilder body = new StringBuilder();

        while (i < len) {
          // Detect closing label at start of a line (possibly indented for PHP 7.3+)
          int lineStart = i;
          int indentLen = 0;
          while (i < len && (code.charAt(i) == ' ' || code.charAt(i) == '\t')) {
            i++;
            indentLen++;
          }
          if (code.regionMatches(i, labelStr, 0, labelStr.length())) {
            int afterLabel = i + labelStr.length();
            // Closing label must be followed by ; or end-of-line/file
            if (afterLabel >= len
              || code.charAt(afterLabel) == ';'
              || code.charAt(afterLabel) == '\n'
              || code.charAt(afterLabel) == '\r') {
              // Strip the indentation from body lines (PHP 7.3+)
              if (indentLen > 0 && body.length() > 0) {
                body = stripHeredocIndent(body, indentLen);
              }
              // Advance past the closing label line
              while (i < len && code.charAt(i) != '\n') {
                curCol++;
                i++;
              }
              if (i < len) {
                curLine++;
                curCol = 0;
                i++;
              }
              break;
            }
          }
          // Not a closing label — restore position and read the line
          i = lineStart;
          while (i < len && code.charAt(i) != '\n') {
            body.append(code.charAt(i));
            curCol++;
            i++;
          }
          if (i < len) {
            body.append('\n');
            curLine++;
            curCol = 0;
            i++;
          }
        }
        result.add(new StringLiteral(body.toString(), bodyLine, bodyCol));
        continue;
      }

      // Double-quoted string
      if (c == '"') {
        curCol++;
        i++;
        int contentLine = curLine;
        int contentCol = curCol;
        StringBuilder sb = new StringBuilder();
        while (i < len) {
          char cc = code.charAt(i);
          if (cc == '\\' && i + 1 < len) {
            char next = code.charAt(i + 1);
            switch (next) {
              case '"':  sb.append('"');  curCol += 2; i += 2; break;
              case '\\': sb.append('\\'); curCol += 2; i += 2; break;
              case 'n':  sb.append('\n'); curCol += 2; i += 2; break;
              case 'r':  sb.append('\r'); curCol += 2; i += 2; break;
              case 't':  sb.append('\t'); curCol += 2; i += 2; break;
              case '$':  sb.append('$');  curCol += 2; i += 2; break;
              default:   sb.append(cc);   curCol++;   i++;     break;
            }
          } else if (cc == '"') {
            curCol++;
            i++;
            break;
          } else {
            sb.append(cc);
            if (cc == '\n') {
              curLine++;
              curCol = 0;
            } else {
              curCol++;
            }
            i++;
          }
        }
        result.add(new StringLiteral(sb.toString(), contentLine, contentCol));
        continue;
      }

      // Single-quoted string
      if (c == '\'') {
        curCol++;
        i++;
        int contentLine = curLine;
        int contentCol = curCol;
        StringBuilder sb = new StringBuilder();
        while (i < len) {
          char cc = code.charAt(i);
          if (cc == '\\' && i + 1 < len) {
            char next = code.charAt(i + 1);
            if (next == '\\' || next == '\'') {
              sb.append(next);
              curCol += 2;
              i += 2;
            } else {
              sb.append(cc);
              curCol++;
              i++;
            }
          } else if (cc == '\'') {
            curCol++;
            i++;
            break;
          } else {
            sb.append(cc);
            if (cc == '\n') {
              curLine++;
              curCol = 0;
            } else {
              curCol++;
            }
            i++;
          }
        }
        result.add(new StringLiteral(sb.toString(), contentLine, contentCol));
        continue;
      }

      // Default: advance
      curCol++;
      i++;
    }

    return result;
  }

  private static StringBuilder stripHeredocIndent(StringBuilder body, int indentLen) {
    String[] lines = body.toString().split("\n", -1);
    StringBuilder stripped = new StringBuilder(body.length());
    for (int li = 0; li < lines.length; li++) {
      String line = lines[li];
      if (line.length() >= indentLen) {
        stripped.append(line, indentLen, line.length());
      } else {
        stripped.append(line);
      }
      if (li < lines.length - 1) {
        stripped.append('\n');
      }
    }
    return stripped;
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

      if (localStart == 1) {
        node.setStartColumnPosition(baseCol + node.getStartColumnPosition());
      }
      if (localEnd == 1) {
        node.setEndColumnPosition(baseCol + node.getEndColumnPosition());
      }

      if (node instanceof TagNode tag) {
        for (Attribute attr : tag.getAttributes()) {
          if (attr.getLine() > 0) {
            attr.setLine(baseLine + attr.getLine() - 1);
          }
        }
      }
    }
  }

  record StringLiteral(String value, int lineOffset, int columnOffset) {
  }
}
