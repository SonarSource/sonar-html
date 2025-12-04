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
package org.sonar.plugins.html.checks.whitespace;

import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.CommentNode;
import org.sonar.plugins.html.node.DirectiveNode;
import org.sonar.plugins.html.node.ExpressionNode;
import org.sonar.plugins.html.node.Node;


@Rule(key = "WhiteSpaceAroundCheck")
public class WhiteSpaceAroundCheck extends AbstractPageCheck {

  private void checkEndWhitespace(Node node, String code, String end) {

    int position = end.length();
    if (code.endsWith(end) && code.length() > position) {
      char ch = code.charAt(code.length() - position - 1);

      if (!Character.isWhitespace(ch)) {
        createViolation(node.getStartLinePosition(), "Add a space at column " + (node.getEndColumnPosition() - position) + ".");
      }
    }
  }

  private void checkStartWhitespace(Node node, String code, String start) {

    int position = start.length();
    if (code.startsWith(start) && code.length() > position) {
      char ch = code.charAt(position);
      if (ch == '!' || ch == '=') {
        handleEqualSign(node, code, position);
      } else if (!Character.isWhitespace(ch)) {
        createStartIssue(node.getStartLinePosition(), node.getStartColumnPosition() + position);
      }
    }
  }

  private void handleEqualSign(Node node, String code, int position) {
    int tmpPosition = position + 1;
    if (code.length() > tmpPosition && !Character.isWhitespace(code.charAt(tmpPosition))) {
      createStartIssue(node.getStartLinePosition(), node.getStartColumnPosition() + tmpPosition);
    }
  }

  private void createStartIssue(int line, int expectedWhitespaceColumn) {
    createViolation(line, "A whitespace is missing after the starting tag at column " + expectedWhitespaceColumn + ".");
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
