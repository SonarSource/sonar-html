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

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import org.sonar.api.internal.apachecommons.lang.StringUtils;
import org.sonar.check.Rule;
import org.sonar.plugins.html.api.accessibility.Aria;
import org.sonar.plugins.html.api.accessibility.Aria.RoleDefinition;
import org.sonar.plugins.html.api.accessibility.AriaProperty;
import org.sonar.plugins.html.api.accessibility.AriaRole;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "S6811")
public class RoleSupportsAriaPropertyCheck extends AbstractPageCheck {
  @Override
  public void startElement(TagNode element) {
    var roleAttr = element.getPropertyValue("role");
    AriaRole[] roles;
    boolean isImplicit;
    if (roleAttr == null) {
      isImplicit = true;
      roles = new AriaRole[]{Aria.getImplicitRole(element)};
    } else {
      isImplicit = false;
      roles = Arrays.stream(roleAttr.split("\\s+")).map(String::trim).map(AriaRole::of).toArray(AriaRole[]::new);
    }

    var rolesProperties = Arrays.stream(roles).map(Aria::getRole).filter(Objects::nonNull).toArray(RoleDefinition[]::new);

    if (rolesProperties.length  == 0) {
      return;
    }

    element.getAttributes().forEach(attr -> {
      var normalizedAttr = attr.getName().toLowerCase(Locale.ROOT);
      var property = Aria.getProperty(AriaProperty.of(normalizedAttr));
      if (property != null && Arrays.stream(rolesProperties).noneMatch(role -> role.propertyIsAllowed(property.getName()))) {
        createViolation(
          element,
          isImplicit ?
            String.format("The attribute %s is not supported by the role %s. This role is implicit on the element %s.",
              attr.getName(), rolesProperties[0].getName(), element.getNodeName()) :
            String.format(
              "The attribute %s is not supported by the role %s.",
              attr.getName(),
              StringUtils.join(Arrays.stream(rolesProperties).map(RoleDefinition::getName).map(AriaRole::toString).toArray(String[]::new), " or ")
            )
        );
      }
    });
  }
}
