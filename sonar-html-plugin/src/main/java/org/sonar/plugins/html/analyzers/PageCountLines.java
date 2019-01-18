/*
 * SonarHTML :: SonarQube Plugin
 * Copyright (c) 2010-2019 SonarSource SA and Matthijs Galesloot
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

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import org.apache.commons.lang.StringUtils;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
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

  private static final Logger LOG = Loggers.get(PageCountLines.class);

  private int blankLines;
  private int headerCommentLines;
  private final Set<Integer> detailedLinesOfCode = Sets.newHashSet();
  private final Set<Integer> detailedLinesOfComments = Sets.newHashSet();

  @Override
  public void startDocument(List<Node> nodes) {
    blankLines = 0;
    headerCommentLines = 0;
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

    LOG.debug("HtmlSensor: " + getHtmlSourceCode().toString() + ": " + detailedLinesOfComments.size() + "," + headerCommentLines + "," + blankLines);
  }

  private void count(List<Node> nodeList) {
    for (int i = 0; i < nodeList.size(); i++) {
      Node node = nodeList.get(i);
      Node previousNode = i > 0 ? nodeList.get(i - 1) : null;
      Node nextNode = i < nodeList.size() - 1 ? nodeList.get(i + 1) : null;
      handleToken(node, previousNode, nextNode);
    }
    addMeasures();
  }

  private void handleToken(Node node, @Nullable Node previousNode, @Nullable Node nextNode) {

    int linesOfCodeCurrentNode = node.getLinesOfCode();
    if (nextNode == null) {
      linesOfCodeCurrentNode++;
    }

    switch (node.getNodeType()) {
      case TAG:
      case DIRECTIVE:
      case EXPRESSION:
        addLineNumbers(node, detailedLinesOfCode);
        break;
      case COMMENT:
        handleTokenComment(node, previousNode, linesOfCodeCurrentNode);
        break;
      case TEXT:
        handleTextToken((TextNode) node, previousNode, linesOfCodeCurrentNode);
        break;
      default:
        break;
    }
  }

  private void handleTokenComment(Node node, @Nullable Node previousNode, int linesOfCodeCurrentNode) {
    if (previousNode == null) {
      // this is a header comment
      headerCommentLines += linesOfCodeCurrentNode;
    } else {
      addLineNumbers(node, detailedLinesOfComments);
    }
  }

  private void handleTextToken(TextNode textNode, @Nullable Node previousNode, int linesOfCodeCurrentNode) {
    handleDetailedTextToken(textNode);
    if (textNode.isBlank() && linesOfCodeCurrentNode > 0) {
      int nonBlankLines = 0;

      // add one newline to the previous node
      if (previousNode != null) {
        switch (previousNode.getNodeType()) {
          case COMMENT:
            nonBlankLines = handleTextTokenComment(previousNode, nonBlankLines);
            break;
          case TAG:
          case DIRECTIVE:
          case EXPRESSION:
            nonBlankLines++;
            break;
          default:
            break;
        }
      }

      // remaining newlines are added to blanklines
      blankLines += linesOfCodeCurrentNode - nonBlankLines;
    }
  }

  private void handleDetailedTextToken(TextNode textNode) {
    String[] element = textNode.getCode().split("\n", -1);
    int startLine = textNode.getStartLinePosition();
    for (int i = 0; i < element.length; i++) {
      if (!StringUtils.isBlank(element[i])) {
        detailedLinesOfCode.add(startLine + i);
      }
    }
  }

  private int handleTextTokenComment(Node previousNode, int nonBlankLines) {
    if (previousNode.getStartLinePosition() == 1) {
      // this was a header comment
      headerCommentLines++;
    }
    return nonBlankLines + 1;
  }

  private static void addLineNumbers(Node node, Set<Integer> detailedLines) {
    for (int i = node.getStartLinePosition(); i <= node.getEndLinePosition(); i++) {
      detailedLines.add(i);
    }
  }
}
