/*
 * SonarSource :: Web :: Sonar Plugin
 * Copyright (c) 2010-2016 SonarSource SA and Matthijs Galesloot
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
package org.sonar.plugins.web.checks.header;

import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.checks.RuleTags;
import org.sonar.plugins.web.node.DirectiveNode;
import org.sonar.plugins.web.node.Node;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;

import java.util.List;

/**
 * @author Matthijs Galesloot
 * @since 1.0
 */
@Rule(
  key = "MultiplePageDirectivesCheck",
  name = "Multiple \"page\" directives should not be used",
  priority = Priority.MINOR,
  tags = {RuleTags.CONVENTION, RuleTags.JSP_JSF})
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.READABILITY)
@SqaleConstantRemediation("2min")
public class MultiplePageDirectivesCheck extends AbstractPageCheck {

  private DirectiveNode node;
  private int pageDirectives;

  @Override
  public void directive(DirectiveNode node) {
    if (!node.isHtml() && "page".equalsIgnoreCase(node.getNodeName()) && !isImportDirective(node)) {
      pageDirectives++;
      this.node = node;
    }
  }

  @Override
  public void endDocument() {
    if (pageDirectives > 1) {
      createViolation(node.getStartLinePosition(), "Combine these " + pageDirectives + " page directives into one.");
    }
  }

  @Override
  public void startDocument(List<Node> nodes) {
    pageDirectives = 0;
  }

  private static boolean isImportDirective(DirectiveNode node) {
    return node.getAttributes().size() == 1 && node.getAttribute("import") != null;
  }

}
