/*
 * SonarQube HTML Plugin :: Sonar Plugin
 * Copyright (C) 2010-2024 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package org.sonar.plugins.html.checks.accessibility;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
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
              String.join(" or ", Arrays.stream(rolesProperties).map(RoleDefinition::getName).map(AriaRole::toString).toArray(String[]::new))
            )
        );
      }
    });
  }
}
