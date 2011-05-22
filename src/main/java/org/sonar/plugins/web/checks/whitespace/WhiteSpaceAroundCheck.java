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

package org.sonar.plugins.web.checks.whitespace;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.node.CommentNode;
import org.sonar.plugins.web.node.DirectiveNode;
import org.sonar.plugins.web.node.ExpressionNode;
import org.sonar.plugins.web.node.Node;

/**
 * Check for required white space around start and end markers.
 *
 * @see http://java.sun.com/developer/technicalArticles/javaserverpages/code_convention/ paragraph Blank Spaces
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
@Rule(key = "WhiteSpaceAroundCheck", name ="Whitespace Around", description = "White space around", priority = Priority.MINOR)
public class WhiteSpaceAroundCheck extends AbstractPageCheck {

  private void checkEndWhitespace(Node node, String code, String end) {

    int position = end.length();
    if (code.endsWith(end) && code.length() > position) {
      char ch = code.charAt(code.length() - position - 1);

      if ( !Character.isWhitespace(ch)) {
        createViolation(node);
      }
    }
  }

  private void checkStartWhitespace(Node node, String code, String start) {

    int position = start.length();
    if (code.startsWith(start) && code.length() > position) {
      char ch = code.charAt(position);
      switch (ch) {
        case '!':
        case '=':
          position++;
          if (code.length() > position && !Character.isWhitespace(code.charAt(position))) {
            createViolation(node);
          }
          break;
        default:
          if ( !Character.isWhitespace(ch)) {
            createViolation(node);
          }
          break;
      }
    }
  }

  @Override
  public void comment(CommentNode node) {

    if (node.isHtml()) {
      checkStartWhitespace(node, node.getCode(), "<!--");
      checkEndWhitespace(node, node.getCode(), "-->");
    } else {
      checkStartWhitespace(node, node.getCode(), "<%--");
      checkEndWhitespace(node, node.getCode(), "--%>");
    }
  }

  @Override
  public void directive(DirectiveNode node) {
    if (node.isJsp()) {
      checkStartWhitespace(node, node.getCode(), "<%@");
      checkEndWhitespace(node, node.getCode(), "%>");
    }
  }

  @Override
  public void expression(ExpressionNode node) {
    checkStartWhitespace(node, node.getCode(), "<%");
    checkEndWhitespace(node, node.getCode(), "%>");
  }
}