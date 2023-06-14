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
