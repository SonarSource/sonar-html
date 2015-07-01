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
package org.sonar.plugins.web.checks.style;

import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.checks.WebRule;
import org.sonar.plugins.web.node.TagNode;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;

/**
 * Checker for occurrence of inline style.
 *
 * @see http://java.sun.com/developer/technicalArticles/javaserverpages/code_convention/paragraphCascading Style Sheets
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
@Rule(
  key = "InlineStyleCheck",
  priority = Priority.MINOR,
  name = "The \"style\" attribute should not be used")
@WebRule(activeByDefault = false)
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.UNDERSTANDABILITY)
@SqaleConstantRemediation("15min")
public class InlineStyleCheck extends AbstractPageCheck {

  @Override
  public void startElement(TagNode element) {

    if ("style".equalsIgnoreCase(element.getNodeName())) {
      createViolation(element.getStartLinePosition(), "Use CSS classes instead.");
    }
  }
}
