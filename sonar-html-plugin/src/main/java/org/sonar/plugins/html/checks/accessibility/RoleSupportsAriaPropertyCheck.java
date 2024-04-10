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
import org.sonar.check.Rule;
import org.sonar.plugins.html.api.accessibility.Aria;
import org.sonar.plugins.html.api.accessibility.AriaProperty;
import org.sonar.plugins.html.api.accessibility.AriaRole;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "S6811")
public class RoleSupportsAriaPropertyCheck extends AbstractPageCheck {
  @Override
  public void startElement(TagNode element) {
    var roleAttr = element.getPropertyValue("role");
    AriaRole role;
    boolean isImplicit;
    if (roleAttr == null) {
      isImplicit = true;
      role = Aria.getImplicitRole(element);
    } else {
      isImplicit = false;
      role = AriaRole.of(roleAttr);
    }

    if (role != null) {
      var roleObj = Aria.getRole(role);
      if (roleObj == null) {
        return;
      }
      String finalRole = role.toString();
      element.getAttributes().forEach(attr -> {
        var normalizedAttr = attr.getName().toLowerCase(Locale.ROOT);
        var property = Aria.getProperty(AriaProperty.of(normalizedAttr));
        if (property != null && !roleObj.propertyIsAllowed(property.getName())) {
          createViolation(
            element,
            isImplicit ?
              String.format("The attribute %s is not supported by the role %s. This role is implicit on the element %s.", attr.getName(), finalRole, element.getNodeName()) :
              String.format("The attribute %s is not supported by the role %s.", attr.getName(), finalRole));
        }
      });
    }

  }

}