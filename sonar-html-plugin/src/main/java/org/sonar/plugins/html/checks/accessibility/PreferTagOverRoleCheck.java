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

import java.util.Locale;
import java.util.stream.Collectors;
import org.sonar.check.Rule;
import org.sonar.plugins.html.api.accessibility.Aria;
import org.sonar.plugins.html.api.accessibility.AriaRole;
import org.sonar.plugins.html.api.accessibility.Element;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "S6819")
public class PreferTagOverRoleCheck extends AbstractPageCheck {
  @Override
  public void startElement(TagNode element) {
    var roleAttr = element.getPropertyValue("role");
    if (roleAttr == null) {
      return;
    }

    // If multiple roles, only use first role defined:
    // https://github.com/jsx-eslint/eslint-plugin-jsx-a11y/blob/0d5321a5457c5f0da0ca216053cc5b4f571b53ae/src/rules/prefer-tag-over-role.js#L49
    AriaRole ariaRole = AriaRole.of(roleAttr.split(" ")[0].toLowerCase(Locale.ROOT));
    Aria.RoleDefinition roleDef = Aria.getRole(ariaRole);
    Element elementObj = Element.of(element.getNodeName().toLowerCase(Locale.ROOT));
    if (
      roleDef == null ||
      roleDef.getElements().isEmpty() ||
      (elementObj != null && roleDef.getElements().contains(elementObj))
    ) {
      return;
    }
    createViolation(element, String.format(
      "Use %s instead of the %s role to ensure accessibility across all devices.",
      roleDef.getElements().stream().map(tag -> "<" + tag + ">").sorted().collect(Collectors.joining(" or ")),
      ariaRole));
  }

}
