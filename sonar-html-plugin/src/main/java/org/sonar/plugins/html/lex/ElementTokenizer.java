/*
 * SonarQube HTML
 * Copyright (C) 2010-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
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
        case '>', '/', '%', '@':
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

      if (isQuote((char) ch)) {
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
      if (character == '>' || character == '/') {
        return true;
      }
      return Character.isWhitespace(character);
    }
  }

  private enum ParseMode {
    BEFORE_ATTRIBUTE_NAME, BEFORE_ATTRIBUTE_VALUE, BEFORE_NODE_NAME
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
