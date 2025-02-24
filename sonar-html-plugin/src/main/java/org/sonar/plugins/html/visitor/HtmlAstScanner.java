/*
 * SonarQube HTML
 * Copyright (C) 2010-2025 SonarSource SA
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
package org.sonar.plugins.html.visitor;

import java.util.ArrayList;
import java.util.List;
import org.sonar.plugins.html.node.CommentNode;
import org.sonar.plugins.html.node.DirectiveNode;
import org.sonar.plugins.html.node.ExpressionNode;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;
import org.sonar.plugins.html.node.TextNode;

/**
 * Scans the nodes of a page and send events to the visitors.
 *

 */
public class HtmlAstScanner {

  private final List<DefaultNodeVisitor> metricVisitors;
  private final List<DefaultNodeVisitor> checkVisitors = new ArrayList<>();

  public HtmlAstScanner(List<DefaultNodeVisitor> metricVisitors) {
    this.metricVisitors = metricVisitors;
  }

  /**
   * Add a visitor to the list of visitors.
   */
  public void addVisitor(DefaultNodeVisitor visitor) {
    checkVisitors.add(visitor);
    visitor.init();
  }

  /**
   * Scan a list of Nodes and send events to the visitors.
   */
  public void scan(List<Node> nodeList, HtmlSourceCode htmlSourceCode) {
    scan(nodeList, htmlSourceCode, metricVisitors);
    scan(nodeList, htmlSourceCode, checkVisitors);
  }

  private static void scan(List<Node> nodeList, HtmlSourceCode htmlSourceCode, List<DefaultNodeVisitor> visitors) {
    // prepare the visitors
    for (DefaultNodeVisitor visitor : visitors) {
      visitor.setSourceCode(htmlSourceCode);
    }

    // notify visitors for a new document
    for (DefaultNodeVisitor visitor : visitors) {
      visitor.startDocument(nodeList);
    }

    // notify the visitors for start and end of element
    for (Node node : nodeList) {
      for (DefaultNodeVisitor visitor : visitors) {
        scanElement(visitor, node);
      }
    }

    // notify visitors for end of document
    for (DefaultNodeVisitor visitor : visitors) {
      visitor.endDocument();
    }
  }

  /**
   * Scan a single element and send appropriate event: start element, end element, characters, comment, expression or directive.
   */
  private static void scanElement(DefaultNodeVisitor visitor, Node node) {
    switch (node.getNodeType()) {
      case TAG:
        scanElementTag(visitor, (TagNode) node);
        break;
      case TEXT:
        visitor.characters((TextNode) node);
        break;
      case COMMENT:
        visitor.comment((CommentNode) node);
        break;
      case EXPRESSION:
        visitor.expression((ExpressionNode) node);
        break;
      case DIRECTIVE:
        visitor.directive((DirectiveNode) node);
        break;
      default:
        break;
    }
  }

  private static void scanElementTag(DefaultNodeVisitor visitor, TagNode node) {
    if (!node.isEndElement()) {
      visitor.startElement(node);
    }
    if (node.isEndElement() || node.hasEnd()) {
      visitor.endElement(node);
    }
  }

}
