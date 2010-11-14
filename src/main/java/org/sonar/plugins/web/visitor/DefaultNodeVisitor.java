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

import java.util.List;

import org.sonar.plugins.web.node.CommentNode;
import org.sonar.plugins.web.node.DirectiveNode;
import org.sonar.plugins.web.node.ExpressionNode;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.node.TagNode;
import org.sonar.plugins.web.node.TextNode;

/**
 * Defines interface for node visitor with default dummy implementations.
 *
 * @author Matthijs Galesloot
 */
public class DefaultNodeVisitor implements NodeVisitor {

  private WebSourceCode webSourceCode;

  public void characters(TextNode textNode) {

  }

  public void comment(CommentNode node) {

  }

  public void directive(DirectiveNode node) {

  }

  public void endDocument() {

  }

  public void endElement(TagNode node) {

  }

  public void expression(ExpressionNode node) {

  }

  public WebSourceCode getWebSourceCode() {
    return webSourceCode;
  }

  public void startDocument(WebSourceCode webSourceCode, List<Node> nodes) {
    startDocument(webSourceCode);
  }

  public void startDocument(WebSourceCode webSourceCode) {
    this.webSourceCode = webSourceCode;
  }

  public void startElement(TagNode node) {

  }
}
