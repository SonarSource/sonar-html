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
package org.sonar.plugins.web.checks.sonar;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.checks.RuleTags;
import org.sonar.plugins.web.checks.WebRule;
import org.sonar.plugins.web.node.TagNode;
import org.sonar.squidbridge.annotations.NoSqale;

@Rule(
  key = "TableHeaderHasIdOrScopeCheck",
  priority = Priority.MAJOR,
  name = "\"th\" table headers tags should have an \"id\" or a \"scope\" attribute")
@WebRule(activeByDefault = false)
@RuleTags({
  RuleTags.ACCESSIBILITY
})
@NoSqale
public class TableHeaderHasIdOrScopeCheck extends AbstractPageCheck {

  @Override
  public void startElement(TagNode node) {
    if (isTableHeader(node) && !hasId(node) && !hasScope(node)) {
      createViolation(node.getStartLinePosition(), "Add either an 'id' or a 'scope' attribute to this <" + node.getNodeName() + "> tag.");
    }
  }

  private static boolean isTableHeader(TagNode node) {
    return "TH".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean hasId(TagNode node) {
    return node.getAttribute("id") != null;
  }

  private static boolean hasScope(TagNode node) {
    return node.getAttribute("scope") != null;
  }

}
