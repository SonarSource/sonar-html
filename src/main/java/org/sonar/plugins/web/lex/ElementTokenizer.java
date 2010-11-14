/*
 * Sonar Web Plugin
 * Copyright (C) 2010 Matthijs Galesloot
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
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
 * @author Matthijs Galesloot
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
            codeReader.startRecording();
            parseNestedTag(codeReader, element);
            element.getAttributes().add(new Attribute(codeReader.stopRecording().toString()));
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
   * The nested tag is added as an attribute to its parent element.
   */
  private void parseNestedTag(CodeReader codeReader, TagNode element) {
    codeReader.pop();

    TagNode nestedTag = new TagNode();

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
            break;
          } else {
            codeReader.pop();
            continue;
          }
        case '>':
          codeReader.pop();
          return; // nested tag ended
        case '/':
        case '%':
        case '@':
          codeReader.pop();
          continue;
        default:
          break;
      }

      mode = parseToken(mode, codeReader, nestedTag);
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
