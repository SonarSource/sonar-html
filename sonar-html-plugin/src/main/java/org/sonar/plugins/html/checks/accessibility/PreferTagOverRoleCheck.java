/*
 * SonarSource HTML analyzer :: Sonar Plugin
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
