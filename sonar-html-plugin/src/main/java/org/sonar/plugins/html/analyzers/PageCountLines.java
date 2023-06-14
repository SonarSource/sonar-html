/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (c) 2010-2023 SonarSource SA and Matthijs Galesloot
 * sonarqube@googlegroups.com
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
package org.sonar.plugins.html.analyzers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TextNode;
import org.sonar.plugins.html.visitor.DefaultNodeVisitor;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

/**
 * Count lines of code in web files.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
public class PageCountLines extends DefaultNodeVisitor {

  private final Set<Integer> detailedLinesOfCode = new HashSet<>();
  private final Set<Integer> detailedLinesOfComments = new HashSet<>();

  @Override
  public void startDocument(List<Node> nodes) {
    detailedLinesOfCode.clear();
    detailedLinesOfComments.clear();

    if(getHtmlSourceCode().shouldComputeMetric()) {
      count(nodes);
    }
  }

  private void addMeasures() {
    HtmlSourceCode htmlSourceCode = getHtmlSourceCode();

    htmlSourceCode.addMeasure(CoreMetrics.NCLOC, detailedLinesOfCode.size());
    htmlSourceCode.addMeasure(CoreMetrics.COMMENT_LINES, detailedLinesOfComments.size());

    htmlSourceCode.setDetailedLinesOfCode(detailedLinesOfCode);
  }

  private void count(List<Node> nodeList) {
    for (int i = 0; i < nodeList.size(); i++) {
      Node node = nodeList.get(i);
      Node previousNode = i > 0 ? nodeList.get(i - 1) : null;
      handleToken(node, previousNode);
    }
    addMeasures();
  }

  private void handleToken(Node node, @Nullable Node previousNode) {
    switch (node.getNodeType()) {
      case TAG:
      case DIRECTIVE:
      case EXPRESSION:
        addLineNumbers(node, detailedLinesOfCode);
        break;
      case COMMENT:
        handleTokenComment(node, previousNode);
        break;
      case TEXT:
        handleDetailedTextToken((TextNode) node);
        break;
      default:
        break;
    }
  }

  private void handleTokenComment(Node node, @Nullable Node previousNode) {
    if (previousNode != null) {
      addLineNumbers(node, detailedLinesOfComments);
    }
  }

  private void handleDetailedTextToken(TextNode textNode) {
    String[] element = textNode.getCode().split("\n", -1);
    int startLine = textNode.getStartLinePosition();
    for (int i = 0; i < element.length; i++) {
      if (!element[i].isBlank()) {
        detailedLinesOfCode.add(startLine + i);
      }
    }
  }

  private static void addLineNumbers(Node node, Set<Integer> detailedLines) {
    for (int i = node.getStartLinePosition(); i <= node.getEndLinePosition(); i++) {
      detailedLines.add(i);
    }
  }
}
