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

package org.sonar.plugins.web.checks.xhtml;

import org.apache.commons.lang.StringUtils;
import org.sonar.check.IsoCategory;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.node.CommentNode;
import org.sonar.plugins.web.node.DirectiveNode;
import org.sonar.plugins.web.node.ExpressionNode;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.node.TagNode;
import org.sonar.plugins.web.node.TextNode;

/**
 * Checker to control the length of the lines.
 *
 * @see http://java.sun.com/developer/technicalArticles/javaserverpages/code_convention/ paragraph
 *
 * @author Matthijs Galesloot
 * @since 1.0
 *
 */
@Rule(key = "MaxLineLengthCheck", name ="Maximum Line Length", description = "Checks the length of a line", priority = Priority.MINOR,
    isoCategory = IsoCategory.Maintainability)
public class MaxLineLengthCheck extends AbstractPageCheck {

  private static final int DEFAULT_MAX_LINE_LENGTH = 120;

  @RuleProperty(key = "maxLength", defaultValue= "120", description = "Maximum number of characters in a line")
  private int maxLength = DEFAULT_MAX_LINE_LENGTH;

  private int currentLineLength;

  private Node currentNode;

  public int getMaxLength() {
    return maxLength;
  }

  public void setMaxLength(int maxLength) {
    this.maxLength = maxLength;
  }

  @Override
  public void characters(TextNode textNode) {
    handleNode(textNode);
  }

  @Override
  public void directive(DirectiveNode node) {
    handleNode(node);
  }

  @Override
  public void comment(CommentNode node) {
    handleNode(node);
  }

  @Override
  public void expression(ExpressionNode node) {
    handleNode(node);
  }

  @Override
  public void startElement(TagNode element) {
    handleNode(element);
  }

  @Override
  public void endDocument() {
    if (currentNode != null) {
      check(currentNode, 0);
    }
  }

  private void handleNode(Node node) {
    currentNode = node;
    String code = node.getCode();
    int startPos = 0;
    int indexPos;
    int newlines = 0;
    while ((indexPos = StringUtils.indexOf(code, '\n', startPos)) >= 0) {
      currentLineLength += indexPos - startPos;
      check(node, newlines);

      startPos = indexPos + 1;
      newlines++;
    }
    if (startPos < code.length()) {
      currentLineLength += code.length() - startPos;
    }
  }

  private void check(Node node, int newlines) {
    if (currentLineLength > maxLength) {
      createViolation(node.getStartLinePosition() + newlines, getRule().getDescription() + ": " + currentLineLength);
    }
    currentLineLength = 0;
  }

}