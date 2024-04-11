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

import org.sonar.check.Rule;
import org.sonar.plugins.html.api.accessibility.Aria;
import org.sonar.plugins.html.api.accessibility.AriaRole;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Attribute;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "S6807")
public class ElementWithRoleShouldHaveRequiredPropertiesCheck extends AbstractPageCheck {
  @Override
  public void startElement(TagNode element) {
    var roleName = element.getAttribute("role");

    if (roleName == null) {
      return;
    }

    var role = Aria.getRole(AriaRole.of(roleName));

    if (role == null) {
      return;
    }

    var attributeNames = element.getAttributes().stream().map(Attribute::getName).toList();
    var requiredProperties = role.getRequiredProperties();

    for (var requiredProperty : requiredProperties) {
      var requiredPropertyName = requiredProperty.toString();

      if (!attributeNames.contains(requiredPropertyName)) {
        createViolation(element, String.format("The attribute \"%s\" is required by the role \"%s\".", requiredPropertyName, roleName));
      }
    }
  }
}
