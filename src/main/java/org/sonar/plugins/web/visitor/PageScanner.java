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

package org.sonar.plugins.web.visitor;

import java.util.ArrayList;
import java.util.List;

import org.sonar.plugins.web.node.CommentNode;
import org.sonar.plugins.web.node.DirectiveNode;
import org.sonar.plugins.web.node.ExpressionNode;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.node.TagNode;
import org.sonar.plugins.web.node.TextNode;

public class PageScanner {

  private final List<DefaultNodeVisitor> visitors = new ArrayList<DefaultNodeVisitor>();

  /**
   * Add a visitor to the list of visitors.
   */
  public void addVisitor(DefaultNodeVisitor visitor) {
    visitors.add(visitor);
  }

  /**
   * Scan a list of Nodes and send events to the visitors.
   */
  public void scan(List<Node> nodeList, WebSourceCode webSourceCode) {

    // notify visitors for a new document
    for (DefaultNodeVisitor visitor : visitors) {
      visitor.startDocument(webSourceCode, nodeList);
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
  private void scanElement(DefaultNodeVisitor visitor, Node node) {
    switch (node.getNodeType()) {
      case Tag:
        TagNode element = (TagNode) node;
        if ( !element.isEndElement()) {
          visitor.startElement(element);
        }
        if (element.isEndElement() || element.hasEnd()) {
          visitor.endElement(element);
        }
        break;
      case Text:
        visitor.characters((TextNode) node);
        break;
      case Comment:
        visitor.comment((CommentNode) node);
        break;
      case Expression:
        visitor.expression((ExpressionNode) node);
        break;
      case Directive:
        visitor.directive((DirectiveNode) node);
        break;
      default:
        break;
    }
  }
}
