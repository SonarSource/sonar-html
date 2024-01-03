/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (c) 2010-2024 SonarSource SA and Matthijs Galesloot
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

import java.util.List;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;

public class CdataTokenizer extends AbstractTokenizer<List<Node>> {

  public CdataTokenizer() {
    super("<![CDATA[", "]]>");
  }

  @Override
  Node createNode() {
    TagNode node = new TagNode();
    node.setNodeName("![CDATA[");
    return node;
  }
}
