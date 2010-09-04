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

package org.sonar.plugins.web.analyzers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.node.TextNode;
import org.sonar.plugins.web.visitor.WebSourceCode;

/**
 * @author Matthijs Galesloot
 */
public class PageCountLines {

  private static final Logger LOG = LoggerFactory.getLogger(PageCountLines.class);

  private int blankLines;
  private int commentLines;
  private int linesOfCode;

  private void addMeasures(WebSourceCode sourceCode) {

    sourceCode.addMeasure(CoreMetrics.LINES, (double) linesOfCode + commentLines + blankLines);
    sourceCode.addMeasure(CoreMetrics.NCLOC, linesOfCode);
    sourceCode.addMeasure(CoreMetrics.COMMENT_LINES, commentLines);

    LOG.debug("WebSensor: " + sourceCode.toString() + ":" + linesOfCode + "," + commentLines + "," + blankLines);
  }

  public void count(List<Node> nodeList, WebSourceCode sourceCode) {
    linesOfCode = 0;
    blankLines = 0;
    commentLines = 0;

    for (int i = 0; i < nodeList.size(); i++) {
      Node node = nodeList.get(i);
      Node previousNode = i > 0? nodeList.get(i - 1) : null;
      Node nextNode = i < nodeList.size() - 1 ? nodeList.get(i) : null;
      handleToken(node, previousNode, nextNode);
    }
    addMeasures(sourceCode);
  }

  private void handleToken(Node node, Node previousNode, Node nextNode) {

    int linesOfCodeCurrentNode = node.getLinesOfCode();
    if (nextNode == null) {
      linesOfCodeCurrentNode ++;
    }

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
        TextNode textNode = (TextNode) node;
        if (textNode.isBlank() && linesOfCodeCurrentNode > 0) {

          // add one newline to the previous node
          if (previousNode != null) {
            switch (previousNode.getNodeType()) {
              case Comment:
                commentLines++;
                linesOfCodeCurrentNode--;
                break;
              case Tag:
              case Directive:
              case Expression:
                linesOfCode++;
                linesOfCodeCurrentNode--;
                break;
              default:
                break;
            }
          }

          // remaining newlines are added to blanklines
          if (linesOfCodeCurrentNode > 0) {
            blankLines += linesOfCodeCurrentNode;
          }
        } else {
          linesOfCode += linesOfCodeCurrentNode;
        }

        break;
      default:
        break;
    }
  }
}
