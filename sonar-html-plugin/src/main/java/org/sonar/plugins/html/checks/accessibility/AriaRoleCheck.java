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
package org.sonar.plugins.html.checks.accessibility;

import static org.sonar.plugins.html.api.HtmlConstants.isAbstractRole;

import org.sonar.check.Rule;
import org.sonar.plugins.html.api.accessibility.AriaRole;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "S6821")
public class AriaRoleCheck extends AbstractPageCheck {
  @Override
  public void startElement(TagNode element) {
    var role = element.getAttribute("role");
    if (role == null) {
      return;
    }
    var values = role.split(" ");
    for (var value : values) {
      AriaRole ariaRole = AriaRole.of(value);
      if (ariaRole == null || isAbstractRole(ariaRole)) {
        createViolation(element, String.format(
            "Elements with ARIA roles must use a valid, non-abstract ARIA role. \"%s\" is not a valid role.",
            value));
      }
    }
  }
}