/*
 * Sonar Web Plugin
 * Copyright (C) 2010 Matthijs Galesloot
 * dev@sonar.codehaus.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sonar.plugins.web.lex;

import java.util.List;
import java.util.Stack;

import org.apache.commons.lang.StringUtils;
import org.sonar.channel.CodeReader;
import org.sonar.channel.EndMatcher;
import org.sonar.plugins.web.node.Attribute;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.node.TagNode;

/**
 * Tokenizer for elements.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
class ElementTokenizer extends AbstractTokenizer<List<Node>> {

  private static final class EndQNameMatcher implements EndMatcher {

    public boolean match(int character) {
      return character == '=' || Character.isWhitespace(character);
    }
  }

  private static final class EndTokenMatcher implements EndMatcher {

    public boolean match(int character) {
      switch (character) {
        case '/':
        case '>':
          return true;
        default:
          break;
      }
      return Character.isWhitespace(character);
    }
  }

  private enum ParseMode {
    BEFORE_ATTRIBUTE_NAME, BEFORE_ATTRIBUTE_VALUE, BEFORE_NODE_NAME
  }


  private static final class QuoteMatcher implements EndMatcher {
    private static final char SINGLE_QUOTE = '\'';
    private static final char DOUBLE_QUOTE = '"';
    private int previousChar;

    private final Stack<Character> startChars = new Stack<Character>();

    QuoteMatcher(char startChar) {
      this.startChars.add(startChar);
    }

    public boolean match(int character) {
      boolean result = false;
      if ((character == SINGLE_QUOTE || character == DOUBLE_QUOTE) && previousChar != '\\') {
        if (startChars.peek() == (char) character) {
          startChars.pop();
        } else {
          startChars.add((char) character);
        }
        result = startChars.size() == 0;
      }
      previousChar = character;
      return result;
    }
  }

  private static EndQNameMatcher endQNameMatcher = new EndQNameMatcher();

  private static EndTokenMatcher endTokenMatcher = new EndTokenMatcher();;

  private static boolean isQuote(char c) {
    return c == '\'' || c == '"';
  }

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
    int ch;
    while ((ch = codeReader.peek()) != -1) {

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
          // found a nested tag
          if (mode == ParseMode.BEFORE_ATTRIBUTE_NAME) {
            parseNestedTag(codeReader, element);
            continue;
          } else {
            codeReader.pop();
            continue;
          }
        case '>':
        case '/':
        case '%':
        case '@':
          codeReader.pop();
          continue;
        default:
          break;
      }

      mode = parseToken(mode, codeReader, element);
    }
  }

  /**
   * Parse a nested tag with PageLexer.
   * The nested tag is added as an attribute to its parent element.
   */
  private void parseNestedTag(CodeReader codeReader, TagNode element) {

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

        StringBuilder sbNodeName = new StringBuilder();
        codeReader.popTo(endTokenMatcher, sbNodeName);
        element.setNodeName(sbNodeName.toString());
        return ParseMode.BEFORE_ATTRIBUTE_NAME;

      case BEFORE_ATTRIBUTE_NAME:

        StringBuilder sbQName = new StringBuilder();
        codeReader.popTo(endQNameMatcher, sbQName);
        element.getAttributes().add(new Attribute(sbQName.toString().trim()));

        return ParseMode.BEFORE_ATTRIBUTE_NAME;

      case BEFORE_ATTRIBUTE_VALUE:

        Attribute attribute = element.getAttributes().get(element.getAttributes().size() - 1);
        StringBuilder sbValue = new StringBuilder();
        int ch = codeReader.peek();

        if (isQuote((char) ch)) {
          codeReader.pop();
          if (codeReader.peek() != ch) {
            codeReader.popTo(new QuoteMatcher((char) ch), sbValue);
            attribute.setValue(unescapeQuotes(sbValue.toString(), (char) ch));
          }
          codeReader.pop();
          attribute.setQuoteChar((char) ch);
        } else {
          codeReader.popTo(endTokenMatcher, sbValue);
          attribute.setValue(sbValue.toString().trim());
        }

        return ParseMode.BEFORE_ATTRIBUTE_NAME;
      default:
        break;
    }

    // can't happen
    return ParseMode.BEFORE_NODE_NAME;
  }

  /**
   * Unescape the quotes from the attribute value.
   */
  private String unescapeQuotes(String value, char ch) {
    return StringUtils.replace(value, "\\" + ch, Character.toString(ch));
  }
}
