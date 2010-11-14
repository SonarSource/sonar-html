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

package org.sonar.plugins.web.checks.jsp;

import org.sonar.check.IsoCategory;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.node.CommentNode;
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
@Rule(key = "WhiteSpaceAroundCheck", name ="Whitespace Around", description = "White space around", priority = Priority.MINOR,
    isoCategory = IsoCategory.Maintainability)
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
        case '@':
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
  public void expression(ExpressionNode node) {
    checkStartWhitespace(node, node.getCode(), "<%");
    checkEndWhitespace(node, node.getCode(), "%>");
  }
}