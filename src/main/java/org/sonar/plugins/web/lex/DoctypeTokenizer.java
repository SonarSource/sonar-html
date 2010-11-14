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

package org.sonar.plugins.web.lex;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.List;

import org.sonar.plugins.web.node.Attribute;
import org.sonar.plugins.web.node.DirectiveNode;
import org.sonar.plugins.web.node.Node;

/**
 * Parse a DOCTYPE node.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
class DoctypeTokenizer extends AbstractTokenizer<List<Node>> {

  public DoctypeTokenizer(String startToken, String endToken) {
    super(startToken, endToken);
  }

  @Override
  protected void addNode(List<Node> nodeList, Node node) {
    super.addNode(nodeList, node);

    parseToken((DirectiveNode) node);
  }

  private void parseToken(DirectiveNode node) {
    String code = node.getCode();
    StreamTokenizer tokenizer = new StreamTokenizer(new StringReader(code));
    tokenizer.quoteChar('"');
    try {
      while (tokenizer.nextToken() != StreamTokenizer.TT_EOF) {
        if (tokenizer.sval != null) {
          if (node.getNodeName() == null) {
            node.setNodeName(tokenizer.sval);
          } else {
            node.getAttributes().add(new Attribute(tokenizer.sval));
          }
        }
      }
    } catch (IOException e) {
      // ignore
      return;
    }
  }

  @Override
  Node createNode() {
    return new DirectiveNode();
  }
}
