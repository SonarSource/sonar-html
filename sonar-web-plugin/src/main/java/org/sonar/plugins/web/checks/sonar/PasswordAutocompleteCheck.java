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
package org.sonar.plugins.web.checks.sonar;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.checks.RuleTags;
import org.sonar.plugins.web.node.TagNode;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;

@Rule(
  key = "S1443",
  name = "\"autocomplete\" should be set to \"off\" on input elements of type \"password\"",
  priority = Priority.CRITICAL,
tags = {RuleTags.HTML5, RuleTags.SECURITY})
@SqaleConstantRemediation("5min")
public class PasswordAutocompleteCheck extends AbstractPageCheck {

  @Override
  public void startElement(TagNode node) {
    if (isPasswordInputTag(node) && !hasAutocompleteAttributeOff(node)) {
      createViolation(node.getStartLinePosition(), "Set the \"autocomplete\" attribute of this password input to \"off\".");
    }
  }

  private static boolean isPasswordInputTag(TagNode node) {
    return "input".equalsIgnoreCase(node.getNodeName()) &&
      "password".equalsIgnoreCase(node.getAttribute("type"));
  }

  private static boolean hasAutocompleteAttributeOff(TagNode node) {
    return "off".equals(node.getAttribute("autocomplete"));
  }

}
