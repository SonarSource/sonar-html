/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (C) 2010-2024 SonarSource SA and Matthijs Galesloot
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
package org.sonar.plugins.html.lex;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.List;

import org.sonar.plugins.html.node.Attribute;
import org.sonar.plugins.html.node.DirectiveNode;
import org.sonar.plugins.html.node.Node;

class DoctypeTokenizer extends AbstractTokenizer<List<Node>> {

  public DoctypeTokenizer(String startToken, String endToken) {
    super(startToken, endToken);
  }

  @Override
  protected void addNode(List<Node> nodeList, Node node) {
    super.addNode(nodeList, node);

    parseToken((DirectiveNode) node);
  }

  private static void parseToken(DirectiveNode node) {
    String code = node.getCode();
    StreamTokenizer tokenizer = new StreamTokenizer(new StringReader(code));
    tokenizer.quoteChar('"');
    try {
      while (tokenizer.nextToken() != StreamTokenizer.TT_EOF) {
        if (tokenizer.sval != null) {
          if (node.getNodeName().isEmpty()) {
            node.setNodeName(tokenizer.sval);
          } else {
            node.getAttributes().add(new Attribute(tokenizer.sval));
          }
        }
      }
    } catch (IOException e) {
      // ignore
    }
  }

  @Override
  Node createNode() {
    return new DirectiveNode();
  }
}
