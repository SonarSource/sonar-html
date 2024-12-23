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
package org.sonar.plugins.html.lex;

import java.io.Reader;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.NodeType;
import org.sonar.plugins.html.node.TagNode;

/**
 * Lexical analysis of Vue.js Single File Components.
 */
public class VueLexer extends PageLexer {

  private static final Object TEMPLATE_LEVEL = new Object();
  private static final String TEMPLATE = "template";

  @Override
  public List<Node> parse(Reader reader) {
    List<Node> nodes = super.parse(reader);
    boolean firstTemplateTag = true;
    List<Node> templateNodes = new LinkedList<>();
    Deque<Object> templateLevels = new LinkedList<>();
    for (Node node : nodes) {
      if (node.getNodeType() == NodeType.TAG) {
        TagNode tagNode = (TagNode) node;
        if (tagNode.equalsElementName(TEMPLATE)) {
          if (tagNode.isEndElement()) {
            if (!templateLevels.isEmpty()) {
              templateLevels.pop();
              if (templateLevels.isEmpty()) {
                break;
              }
            }
          } else if (!tagNode.hasEnd()) {
            templateLevels.push(TEMPLATE_LEVEL);
          }
        }
      }
      if (!templateLevels.isEmpty()) {
        if (firstTemplateTag) {
          firstTemplateTag = false;
        } else {
          templateNodes.add(node);
        }
      }
    }
    return templateNodes;
  }
}
