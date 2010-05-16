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

package org.sonar.plugins.web.visitor;

import org.sonar.api.measures.CoreMetrics;
import org.sonar.plugins.web.WebUtils;
import org.sonar.plugins.web.node.CommentNode;
import org.sonar.plugins.web.node.DirectiveNode;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.node.TagNode;
import org.sonar.plugins.web.node.TextNode;

/**
 * @author Matthijs Galesloot
 */
public class PageCountLines extends AbstractNodeVisitor {

  private int blankLines;
  private int commentLines;
  private Class<?> currentElementType;
  private int linesOfCode;

  @Override
  public void characters(TextNode textNode) {
    handleToken(textNode);
  }

  @Override
  public void comment(CommentNode commentNode) {
    handleToken(commentNode);
  }

  private void computeMetrics() {

    getWebSourceCode().addMeasure(CoreMetrics.LINES, (double) linesOfCode + commentLines + blankLines);
    getWebSourceCode().addMeasure(CoreMetrics.NCLOC, linesOfCode);
    getWebSourceCode().addMeasure(CoreMetrics.COMMENT_LINES, commentLines);

    WebUtils.LOG.debug("WebSensor: " + getWebSourceCode().toString() + ":" + linesOfCode + "," + commentLines + "," + blankLines);
  }

  @Override
  public void directive(DirectiveNode node) {
    handleToken(node);
  }

  @Override
  public void endDocument() {
    super.endDocument();

    computeMetrics();
  }

  @Override
  public void endElement(TagNode element) {
    handleToken(element);
  }

  private void handleToken(Node node) {

    int linesOfCodeCurrentNode = node.getLinesOfCode();

    switch (node.getNodeType()) {
      case Tag:
      case Directive:
      case Expression:
        linesOfCode += linesOfCodeCurrentNode;
        break;
      case Comment:
        commentLines += linesOfCodeCurrentNode;
        break;
      case Text:
        if (((TextNode) node).isBlank()) {
          if (CommentNode.class.equals(currentElementType)) {
            commentLines++;
            linesOfCodeCurrentNode--;
          } else if (TagNode.class.equals(currentElementType) || DirectiveNode.class.equals((currentElementType))) {
            linesOfCode++;
            linesOfCodeCurrentNode--;
          }
        }
        if (linesOfCodeCurrentNode > 0) {
          blankLines += linesOfCodeCurrentNode;
        }
        break;
    }
    currentElementType = node.getClass();
  }

  @Override
  public void startDocument(WebSourceCode webSourceCode) {
    super.startDocument(webSourceCode);

    linesOfCode = 0;
    blankLines = 0;
    commentLines = 0;
    currentElementType = null;
  }

  @Override
  public void startElement(TagNode element) {
    handleToken(element);
  }

}
