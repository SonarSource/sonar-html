/*
 * SonarQube HTML
 * Copyright (C) 2010-2025 SonarSource SA
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
import java.util.Objects;
import org.sonar.check.Rule;
import org.sonar.plugins.html.api.Helpers;
import org.sonar.plugins.html.api.accessibility.Aria;
import org.sonar.plugins.html.api.accessibility.AriaRole;
import org.sonar.plugins.html.api.accessibility.Aria.RoleDefinition;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Attribute;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "S6807")
public class ElementWithRoleShouldHaveRequiredPropertiesCheck extends AbstractPageCheck {
  @Override
  public void startElement(TagNode element) {
    var roleName = element.getAttribute("role");

    if (roleName == null || Helpers.isDynamicValue(roleName)) {
      return;
    }

    var attributeNames = element.getAttributes().stream().map(Attribute::getName).toList();
    var roles = Arrays.stream(roleName.split("\\s+"))
      .map(String::trim)
      .map(AriaRole::of)
      .filter(Objects::nonNull)
      .map(Aria::getRole)
      .filter(Objects::nonNull)
      .toArray(RoleDefinition[]::new);

    for (var role : roles) {
      var requiredProperties = role.getRequiredProperties();
      for (var requiredProperty : requiredProperties) {
        var requiredPropertyName = requiredProperty.toString();

        if (!attributeNames.contains(requiredPropertyName)) {
          createViolation(element, String.format("The attribute \"%s\" is required by the role \"%s\".", requiredPropertyName, role.getName()));
        }
      }
    }
  }
}
