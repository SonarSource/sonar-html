/*
 * SonarSource :: Web :: Sonar Plugin
 * Copyright (c) 2010-2017 SonarSource SA and Matthijs Galesloot
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
package org.sonar.plugins.web.checks.sonar;

import org.sonar.check.Rule;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.node.DirectiveNode;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.node.TagNode;

import java.util.List;

@Rule(key = "DoctypePresenceCheck")
public class DoctypePresenceCheck extends AbstractPageCheck {

  private boolean foundDoctype;
  private boolean reported;

  @Override
  public void startDocument(List<Node> nodes) {
    foundDoctype = false;
    reported = false;
  }

  @Override
  public void directive(DirectiveNode node) {
    if (isDoctype(node)) {
      foundDoctype = true;
    }
  }

  private static boolean isDoctype(DirectiveNode node) {
    return "DOCTYPE".equalsIgnoreCase(node.getNodeName());
  }

  @Override
  public void startElement(TagNode node) {
    if (isHtml(node) && !foundDoctype && !reported) {
      createViolation(node.getStartLinePosition(), "Insert a <!DOCTYPE> declaration to before this <" + node.getNodeName() + "> tag.");
      reported = true;
    }
  }

  private static boolean isHtml(TagNode node) {
    return "HTML".equalsIgnoreCase(node.getNodeName());
  }

}
