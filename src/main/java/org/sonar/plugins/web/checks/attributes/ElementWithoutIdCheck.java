/*
 * SonarQube Web Plugin
 * Copyright (C) 2010 SonarSource and Matthijs Galesloot
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
package org.sonar.plugins.web.checks.attributes;

import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.checks.RuleTags;
import org.sonar.plugins.web.node.TagNode;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;

@Rule(
  key = "ElementWithoutIdCheck",
  priority = Priority.MAJOR,
  name = "Elements should have id",
  tags = {RuleTags.TESTS})
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.TESTABILITY_COMPLIANCE)
@SqaleConstantRemediation("5min")
public class ElementWithoutIdCheck extends AbstractPageCheck {

  private static final String DEFAULT_REGEXP = "(\\s*\\w*\\s*:)*\\s*(form|data|button|link|calendar|input(Text|TextArea)?|table|select|command(Button|Link))";

  @RuleProperty(
    key = "regexp",
    description = "Regular expression to atributes that should have id",
    defaultValue = DEFAULT_REGEXP)
  public String regexp = DEFAULT_REGEXP;

  @Override
  public void startElement(TagNode node) {
    if (isElementWhoNeedsId(node) && hasBlankIdValue(node)) {
      createViolation(node.getStartLinePosition(), "Set \"id\" attribute for this field.");
    }
  }
  
  private boolean isElementWhoNeedsId(TagNode node) {
	  return node.getNodeName().matches(regexp);
  }

  private boolean hasBlankIdValue(TagNode node) {
    return node.getAttribute("id") == null;
  }

}
