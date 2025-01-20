/*
 * SonarQube HTML
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

import static org.sonar.plugins.html.api.HtmlConstants.isAbstractRole;

import org.sonar.check.Rule;
import org.sonar.plugins.html.api.Helpers;
import org.sonar.plugins.html.api.accessibility.AriaRole;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "S6821")
public class AriaRoleCheck extends AbstractPageCheck {
  @Override
  public void startElement(TagNode element) {
    var role = element.getAttribute("role");
    if (role == null || Helpers.isDynamicValue(role, getHtmlSourceCode())) {
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
