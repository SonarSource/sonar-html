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
import java.util.Deque;
import java.util.List;
import java.util.Set;
import org.sonar.plugins.html.node.Attribute;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;
import org.sonar.sslr.channel.CodeReader;
import org.sonar.sslr.channel.EndMatcher;

/**
 * Tokenizer for elements.
 *

 */
class ElementTokenizer extends AbstractTokenizer<List<Node>> {

  private static EndQNameMatcher endQNameMatcher = new EndQNameMatcher();
  private static EndTokenMatcher endTokenMatcher = new EndTokenMatcher();
  private static EndUnquotedAttributeMatcher endUnquotedAttributeMatcher = new EndUnquotedAttributeMatcher();

  public ElementTokenizer(String startToken, String endToken) {
    super(startToken, endToken);
  }

  @Override
  protected void addNode(List<Node> nodeList, Node node) {
    super.addNode(nodeList, node);

    parseToken(node);
  }

  @Override
  Node createNode() {
    return new TagNode();
  }

  private void parseToken(Node node) {
    TagNode element = (TagNode) node;

    CodeReader codeReader = new CodeReader(node.getCode());

    ParseMode mode = ParseMode.BEFORE_NODE_NAME;
    for (int ch = codeReader.peek(); ch != -1; ch = codeReader.peek()) {

      // handle white space
      if (Character.isWhitespace(ch)) {
        codeReader.pop();
        continue;
      }

      // handle special characters
      switch (ch) {
        case '=':
          mode = ParseMode.BEFORE_ATTRIBUTE_VALUE;
          codeReader.pop();
          continue;
        case '<':
          nestedTag(element, codeReader, mode);
          continue;
        case '>', '/', '%', '@', '{', '}':
          codeReader.pop();
          continue;
        default:
          break;
      }

      mode = parseToken(mode, codeReader, element);
    }
  }

  private static void nestedTag(TagNode element, CodeReader codeReader, ParseMode mode) {
    // found a nested tag
    if (mode == ParseMode.BEFORE_ATTRIBUTE_NAME) {
      parseNestedTag(codeReader, element);
    } else {
      codeReader.pop();
    }
  }

  /**
   * Parse a nested tag with PageLexer.
   * The nested tag is added as an attribute to its parent element.
   */
  private static void parseNestedTag(CodeReader codeReader, TagNode element) {

    PageLexer nestedPageLexer = new PageLexer();
    List<Node> nodeList = nestedPageLexer.nestedParse(codeReader);

    // add the nested tags as attribute.
    for (Node node : nodeList) {
      element.getAttributes().add(new Attribute(node.getCode()));
    }
  }

  private ParseMode parseToken(ParseMode mode, CodeReader codeReader, TagNode element) {
    switch (mode) {
      case BEFORE_NODE_NAME:
        handleBeforeNodeName(codeReader, element);
        return ParseMode.BEFORE_ATTRIBUTE_NAME;

      case BEFORE_ATTRIBUTE_NAME:
        handleBeforeAttributeName(codeReader, element);
        return ParseMode.BEFORE_ATTRIBUTE_NAME;

      case BEFORE_ATTRIBUTE_VALUE:
        handleBeforeAttributeValue(codeReader, element);
        return ParseMode.BEFORE_ATTRIBUTE_NAME;

      default:
        break;
    }
    // can't happen
    return ParseMode.BEFORE_NODE_NAME;
  }

  private static void handleBeforeAttributeValue(CodeReader codeReader, TagNode element) {
    Attribute attribute;
    if (!element.getAttributes().isEmpty()) {
      attribute = element.getAttributes().get(element.getAttributes().size() - 1);
      StringBuilder sbValue = new StringBuilder();
      int ch = codeReader.peek();

      if (isEscapedQuoteOpener(codeReader)) {
        handleEscapedQuotedAttributeValue(codeReader, attribute, sbValue);
      } else if (isQuote((char) ch)) {
        codeReader.pop();
        if (codeReader.peek() != ch) {
          QuoteMatcher quoteMatcher = new QuoteMatcher((char) ch);
          quoteMatcher.match(codeReader.peek());
          popTo(codeReader, quoteMatcher, sbValue);
          attribute.setValue(unescapeQuotes(sbValue.toString(), (char) ch));
        }
        codeReader.pop();
        attribute.setQuoteChar((char) ch);
      } else {
        popTo(codeReader, endUnquotedAttributeMatcher, sbValue);
        attribute.setValue(sbValue.toString().trim());
      }
    }
  }

  /**
   * Tells whether the upcoming two characters are a backslash followed by a quote.
   * @param codeReader the reader, positioned at the candidate backslash
   * @return true if peek(2) is {@code \"} or {@code \'}
   */
  private static boolean isEscapedQuoteOpener(CodeReader codeReader) {
    char[] next = codeReader.peek(2);
    return next.length == 2 && next[0] == '\\' && isQuote(next[1]);
  }

  /**
   * Reads an attribute value whose delimiters are backslash-escaped quotes — the shape produced
   * when HTML is embedded in a host-language string literal (e.g. PHP {@code "<a href=\"x\">"}).
   * Stops at the first non-escaped backslash-quote pair and stores the unescaped value.
   * @param codeReader the reader, positioned at the opening backslash
   * @param attribute the attribute receiving the parsed value and quote char
   * @param sbValue scratch buffer used to accumulate the raw content
   */
  private static void handleEscapedQuotedAttributeValue(CodeReader codeReader, Attribute attribute, StringBuilder sbValue) {
    codeReader.pop();
    char quote = (char) codeReader.pop();
    attribute.setQuoteChar(quote);
    if (isEscapedQuoteOpener(codeReader)) {
      attribute.setValue("");
      codeReader.pop();
      codeReader.pop();
      return;
    }
    EscapedQuoteMatcher matcher = new EscapedQuoteMatcher(quote);
    matcher.match(codeReader.peek());
    popTo(codeReader, matcher, sbValue);
    if (sbValue.length() > 0 && sbValue.charAt(sbValue.length() - 1) == '\\') {
      sbValue.deleteCharAt(sbValue.length() - 1);
    }
    attribute.setValue(unescapeBackslashEscapedString(sbValue.toString()));
    codeReader.pop();
  }

  private static void handleBeforeAttributeName(CodeReader codeReader, TagNode element) {
    Attribute attribute;
    StringBuilder sbQName = new StringBuilder();
    popTo(codeReader, endQNameMatcher, sbQName);
    attribute = new Attribute(sbQName.toString().trim());
    attribute.setLine(codeReader.getLinePosition() + element.getStartLinePosition() - 1);
    element.getAttributes().add(attribute);
  }

  private static void handleBeforeNodeName(CodeReader codeReader, TagNode element) {
    StringBuilder sbNodeName = new StringBuilder();
    popTo(codeReader, endTokenMatcher, sbNodeName);
    element.setNodeName(sbNodeName.toString());
  }

  /**
   * Unescape the quotes from the attribute value.
   */
  private static String unescapeQuotes(String value, char ch) {
    return value.replaceAll("\\\\" + ch, Character.toString(ch));
  }

  /**
   * Decodes the common backslash escape sequences inside a host-language double-quoted literal
   * (PHP, JS, Java, C#, ...). Unknown escape sequences are kept verbatim, matching PHP behavior
   * where {@code \z} stays as {@code \z}.
   * @param value raw content read between the escaped opening and closing quotes
   * @return the decoded value
   */
  private static String unescapeBackslashEscapedString(String value) {
    StringBuilder out = new StringBuilder(value.length());
    for (int i = 0; i < value.length(); i++) {
      char c = value.charAt(i);
      if (c == '\\' && i + 1 < value.length()) {
        char next = value.charAt(i + 1);
        switch (next) {
          case '\\': out.append('\\'); i++; break;
          case '"':  out.append('"'); i++; break;
          case '\'': out.append('\''); i++; break;
          case 'n':  out.append('\n'); i++; break;
          case 'r':  out.append('\r'); i++; break;
          case 't':  out.append('\t'); i++; break;
          case '$':  out.append('$'); i++; break;
          default:   out.append(c); break;
        }
      } else {
        out.append(c);
      }
    }
    return out.toString();
  }

  private static boolean isQuote(char c) {
    return c == '\'' || c == '"';
  }

  private static final class EndQNameMatcher implements EndMatcher {

    @Override
    public boolean match(int character) {
      return character == '=' || character == '>' || Character.isWhitespace(character);
    }
  }

  private static final class EndUnquotedAttributeMatcher implements EndMatcher {
    private static final Set<Character> FORBIDDEN = Set.of(
      '"',
      '\'',
      '=',
      '<',
      '>',
      '`'
    );

    @Override
    public boolean match(int character) {
      return Character.isWhitespace(character) || FORBIDDEN.contains((char) character);
    }
  }

  private static final class EndTokenMatcher implements EndMatcher {

    @Override
    public boolean match(int character) {
      if (character == '>' || character == '/' || character == '{') {
        return true;
      }
      return Character.isWhitespace(character);
    }
  }

  private enum ParseMode {
    BEFORE_ATTRIBUTE_NAME, BEFORE_ATTRIBUTE_VALUE, BEFORE_NODE_NAME
  }

  /**
   * Matches the end of an escaped-quoted attribute value where the delimiter is a backslash-quote
   * pair, treating any backslash-prefixed character within the value as a single escape unit.
   * Mirrors {@link QuoteMatcher}'s bracket-depth tracking so that a same-quote escape pair inside
   * brackets (e.g. {@code \"alert(\"x\")\"}) is treated as nested rather than closing the value.
   */
  private static final class EscapedQuoteMatcher implements EndMatcher {
    private final char quote;
    private boolean activeBackslash = false;
    private int bracketDepth = 0;

    EscapedQuoteMatcher(char quote) {
      this.quote = quote;
    }

    @Override
    public boolean match(int character) {
      if (activeBackslash) {
        activeBackslash = false;
        return character == quote && bracketDepth == 0;
      }
      if (character == '\\') {
        activeBackslash = true;
        return false;
      }
      if (character == '(' || character == '[') {
        bracketDepth++;
      } else if ((character == ')' || character == ']') && bracketDepth > 0) {
        bracketDepth--;
      }
      return false;
    }
  }

  /**
   * Matches the end of a quoted attribute value, handling nested quotes.
   *
   * Supports two types of nested quote patterns:
   * 1. Different quote types: {@code class="<c:if test='${x}'>..."}
   * 2. Same quotes inside brackets (Razor): {@code id="@Html.UniqueId("field")"} or {@code value="@dict["key"]"}
   *
   * Uses a stack to track quote nesting and bracket depth for method calls and indexers.
   */
  private static final class QuoteMatcher implements EndMatcher {
    private static final char SINGLE_QUOTE = '\'';
    private static final char DOUBLE_QUOTE = '"';
    private int previousChar;
    private final char outerQuote;
    private int bracketDepth = 0;

    private final Deque<Character> quoteStack = new ArrayDeque<>();

    QuoteMatcher(char startChar) {
      this.outerQuote = startChar;
      this.quoteStack.addFirst(startChar);
    }

    @Override
    public boolean match(int character) {
      // Track bracket depth for method calls (...) and indexers [...]
      if (previousChar != '\\') {
        if (character == '(' || character == '[') {
          bracketDepth++;
        } else if ((character == ')' || character == ']') && bracketDepth > 0) {
          bracketDepth--;
        }
      }

      if ((character == SINGLE_QUOTE || character == DOUBLE_QUOTE) && previousChar != '\\') {
        Character topQuote = quoteStack.peekFirst();

        // Check for same-quote-inside-brackets FIRST (Razor pattern)
        // e.g., id="@Html.Method("field")" or value="@dict["key"]"
        if (bracketDepth > 0 && character == outerQuote && quoteStack.size() == 1) {
          quoteStack.addFirst((char) character);
        } else if (topQuote != null && topQuote == (char) character) {
          // Same quote as top of stack - this is a closing quote
          quoteStack.removeFirst();
        } else {
          // Different quote type - opens a new nested level
          quoteStack.addFirst((char) character);
        }

        if (quoteStack.isEmpty()) {
          previousChar = character;
          return true;
        }
      }
      previousChar = character;
      return false;
    }
  }

}
