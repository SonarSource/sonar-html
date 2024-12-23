/*
 * SonarQube HTML
 * Copyright (C) 2010-2024 SonarSource SA
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
      case TAG, DIRECTIVE, EXPRESSION:
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
