/*
 * Copyright (C) 2010 Matthijs Galesloot
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

import org.sonar.channel.Channel;
import org.sonar.channel.CodeReader;
import org.sonar.channel.EndMatcher;
import org.sonar.plugins.web.node.Attribute;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.node.TagNode;

/**
 * @author Matthijs Galesloot
 */
class ElementTokenizer extends AbstractTokenizer implements Channel<List<Node>> {

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
      }
      return Character.isWhitespace(character);
    }
  }
  
  private enum ParseMode {
    BEFORE_ATTRIBUTE_NAME, BEFORE_ATTRIBUTE_VALUE, BEFORE_NODE_NAME
  }

  private static final class QuoteMatcher implements EndMatcher {

    private char startChar;

    QuoteMatcher(char startChar) {
      this.startChar = startChar;
    }

    public boolean match(int character) {

      return character == startChar;
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
    int c;
    while ((c = codeReader.peek()) != -1) {

      // handle white space
      if (Character.isWhitespace(c)) {
        codeReader.pop();
        continue;
      }
      
      // handle special characters
      switch (c) {
        case '=':
          mode = ParseMode.BEFORE_ATTRIBUTE_VALUE;
          codeReader.pop();
          continue;
        case '<':
        case '>':
        case '/':
          codeReader.pop();
          continue;
        default:
          break;
      }

      switch (mode) {
        case BEFORE_NODE_NAME:

          StringBuilder sbNodeName = new StringBuilder();
          codeReader.popTo(endTokenMatcher, sbNodeName);
          element.setNodeName(sbNodeName.toString());
          mode = ParseMode.BEFORE_ATTRIBUTE_NAME;

          break;
        case BEFORE_ATTRIBUTE_NAME:

          StringBuilder sbQName = new StringBuilder();
          codeReader.popTo(endQNameMatcher, sbQName);
          element.getAttributes().add(new Attribute(sbQName.toString().trim()));

          break;
        case BEFORE_ATTRIBUTE_VALUE:

          StringBuilder sbValue = new StringBuilder();
          if (isQuote((char) c)) {
            codeReader.pop();
            codeReader.popTo(new QuoteMatcher((char) c), sbValue);
            codeReader.pop();
          } else {
            codeReader.popTo(endTokenMatcher, sbValue);
          }
          String value = sbValue.toString().trim();
          element.getAttributes().get(element.getAttributes().size() - 1).setValue(value);
          mode = ParseMode.BEFORE_ATTRIBUTE_NAME;

          break;
      }
    }
  }
}
