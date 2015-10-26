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
package org.sonar.plugins.web.checks.dependencies;

import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.checks.RuleTags;
import org.sonar.plugins.web.node.TagNode;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;

@Rule(
  key = "DynamicJspIncludeCheck",
  name = "Dynamic includes should not be used",
  priority = Priority.CRITICAL,
  tags = {RuleTags.JSP_JSF})
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.CPU_EFFICIENCY)
@SqaleConstantRemediation("5min")
public class DynamicJspIncludeCheck extends AbstractPageCheck {

  @Override
  public void startElement(TagNode node) {
    if ("jsp:include".equals(node.getNodeName())) {
      createViolation(node.getStartLinePosition(), "Use an include action instead of an include directive.");
    }
  }

}
