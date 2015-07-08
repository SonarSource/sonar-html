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
package org.sonar.plugins.web.checks.scripting;

import org.apache.commons.lang.StringUtils;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.checks.RuleTags;
import org.sonar.plugins.web.checks.WebRule;
import org.sonar.plugins.web.node.ExpressionNode;
import org.sonar.plugins.web.node.TagNode;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;

@Rule(
  key = "JspScriptletCheck",
  priority = Priority.MAJOR,
  name = "JSP expressions should not be used",
tags = {RuleTags.JSP_JSF, RuleTags.OBSOLETE})
@WebRule(activeByDefault = false)
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.ARCHITECTURE_RELIABILITY)
@SqaleConstantRemediation("30min")
public class JspScriptletCheck extends AbstractPageCheck {

  @Override
  public void expression(ExpressionNode node) {
    String content = trimScriptlet(node.getCode());

    if (StringUtils.isNotBlank(content)) {
      createIssue(node.getStartLinePosition());
    }
  }

  @Override
  public void startElement(TagNode element) {
    if (StringUtils.equalsIgnoreCase(element.getLocalName(), "scriptlet")) {
      createIssue(element.getStartLinePosition());
    }
  }

  private static String trimScriptlet(String code) {
    return StringUtils.removeEnd(StringUtils.removeStart(code, "<%"), "%>");
  }

  private void createIssue(int line) {
    createViolation(line, "Replace this scriptlet using tag libraries and expression language.");
  }

}
