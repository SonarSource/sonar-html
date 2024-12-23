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

import static org.sonar.plugins.html.api.accessibility.Aria.getImplicitRole;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.html.api.Helpers;
import org.sonar.plugins.html.api.accessibility.AriaRole;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "S6822")
public class NoRedundantRolesCheck extends AbstractPageCheck {

  private static final String DEFAULT_ALLOWED_REDUNDANT_ROLES = "nav=navigation";
  @RuleProperty(
    key = "allowedRedundantRoles",
    description = "List of coma-separated pairs of tag and role that are allowed to be redundant. Example: nav=navigation,button=button",
    defaultValue = DEFAULT_ALLOWED_REDUNDANT_ROLES)
  public String allowedRedundantRoles = DEFAULT_ALLOWED_REDUNDANT_ROLES;

  private Map<String, Set<AriaRole>> allowedRedundantRolesMap;

  @Override
  public void startDocument(List<Node> nodes) {
    this.allowedRedundantRolesMap = parseAllowedRedundantRoles(allowedRedundantRoles);
  }

  @Override
  public void startElement(TagNode element) {
    var implicitRole = getImplicitRole(element);
    var explicitRoleRaw = element.getAttribute("role");
    if (explicitRoleRaw == null || Helpers.isDynamicValue(explicitRoleRaw)) {
      return;
    }
    var explicitRole = AriaRole.of(explicitRoleRaw.toLowerCase(Locale.ROOT));

    if (implicitRole == null || explicitRole == null) {
      return;
    }

    if (implicitRole.equals(explicitRole)) {
      if (this.allowedRedundantRolesMap.getOrDefault(element.getNodeName(), new HashSet<>())
          .contains(implicitRole)) {
        return;
      }
      createViolation(element,
          String.format(
              "The element %s has an implicit role of %s. Definig this explicitly is redundant and should be avoided.",
              element.getNodeName(), implicitRole));
    }
  }

  private static Map<String, Set<AriaRole>> parseAllowedRedundantRoles(String allowedRedundantRoles) {
    String[] pairs = allowedRedundantRoles.split(",");
    var map = new HashMap<String, Set<AriaRole>>();
    for (String pair : pairs) {
      String[] parts = pair.split("=");
      if (parts.length != 2) {
        continue;
      }
      String tag = parts[0].trim();
      String role = parts[1].trim();
      map.computeIfAbsent(tag, k -> new HashSet<AriaRole>()).add(AriaRole.of(role));
    }
    return map;
  }
}
