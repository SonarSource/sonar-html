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

package org.sonar.plugins.web.checks.xhtml;

import org.apache.commons.lang.StringUtils;
import org.sonar.check.Check;
import org.sonar.check.CheckProperty;
import org.sonar.check.IsoCategory;
import org.sonar.check.Priority;
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
@Check(key = "MaxLineLengthCheck", title = "Maximum Line Length", 
    description = "Checks the lenght of a line", priority = Priority.MINOR, isoCategory = IsoCategory.Maintainability)
public class MaxLineLengthCheck extends AbstractPageCheck {

  private static final int DEFAULT_MAX_LINE_LENGTH = 120;

  @CheckProperty(key = "maxLength", title = "Max line length", description = "Maximum number of characters in a line")
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
    while ((indexPos = StringUtils.indexOf(code, '\n' , startPos)) >= 0) {
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