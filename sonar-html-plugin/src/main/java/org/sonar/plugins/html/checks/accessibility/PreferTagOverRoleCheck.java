/*
 * SonarQube HTML
 * Copyright (C) SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * You can redistribute and/or modify this program under the terms of
 * the Sonar Source-Available License Version 1, as published by SonarSource Sàrl.
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

import static org.sonar.plugins.html.api.HtmlConstants.hasKnownHTMLTag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.html.api.accessibility.Aria;
import org.sonar.plugins.html.api.accessibility.AriaRole;
import org.sonar.plugins.html.api.accessibility.Element;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "S6819")
public class PreferTagOverRoleCheck extends AbstractPageCheck {

  private static final Logger LOG = LoggerFactory.getLogger(PreferTagOverRoleCheck.class);

  private static final String DEFAULT_ALLOWED_ROLES = "";

  static final String UNKNOWN_ALLOWED_ROLE_WARNING =
    "Rule S6819 \"allowedRoles\" parameter contains unknown ARIA roles: %s. These entries will have no effect.";

  @RuleProperty(
    key = "allowedRoles",
    description = "Comma-separated list of ARIA roles to allow on HTML elements even when an equivalent HTML tag exists. " +
      "Values are matched case-insensitively. Unknown role names are ignored.",
    defaultValue = DEFAULT_ALLOWED_ROLES)
  public String allowedRoles = DEFAULT_ALLOWED_ROLES;

  private String parsedAllowedRolesSource;
  private Set<AriaRole> allowedRolesCache = Set.of();
  private List<String> unknownAllowedRoles = List.of();

  @Override
  public void startDocument(List<Node> nodes) {
    refreshAllowedRolesCache();
  }

  @Override
  public void startElement(TagNode element) {
    var roleAttr = element.getPropertyValue("role");
    if (roleAttr == null || roleAttr.isBlank() || !hasKnownHTMLTag(element)) {
      return;
    }

    // The HTML "role" attribute is a fallback list: user agents pick the first
    // non-abstract role they support. We replicate that by walking the tokens
    // and using the first one mapped to a concrete RoleDefinition.
    Aria.RoleDefinition roleDef = resolveFirstApplicableRole(roleAttr);
    if (roleDef == null || roleDef.getElements().isEmpty()) {
      return;
    }

    AriaRole ariaRole = roleDef.getName();
    if (allowedRolesCache.contains(ariaRole)) {
      return;
    }

    Element elementObj = Element.of(element.getNodeName().toLowerCase(Locale.ROOT));
    if (elementObj != null && roleDef.getElements().contains(elementObj)) {
      return;
    }
    createViolation(element, String.format(
      "Use %s instead of the %s role to ensure accessibility across all devices.",
      roleDef.getElements().stream().map(tag -> "<" + tag + ">").sorted().collect(Collectors.joining(" or ")),
      ariaRole));
  }

  @Override
  public List<String> collectAnalysisWarnings() {
    refreshAllowedRolesCache();
    if (unknownAllowedRoles.isEmpty()) {
      return List.of();
    }
    return List.of(String.format(UNKNOWN_ALLOWED_ROLE_WARNING, String.join(", ", unknownAllowedRoles)));
  }

  private static Aria.RoleDefinition resolveFirstApplicableRole(String roleAttr) {
    for (String token : roleAttr.trim().split("\\s+")) {
      AriaRole role = AriaRole.of(token.toLowerCase(Locale.ROOT));
      if (role == null) {
        continue;
      }
      Aria.RoleDefinition def = Aria.getRole(role);
      if (def != null && !def.getElements().isEmpty()) {
        return def;
      }
    }
    return null;
  }

  private void refreshAllowedRolesCache() {
    String current = allowedRoles == null ? "" : allowedRoles;
    if (current.equals(parsedAllowedRolesSource)) {
      return;
    }
    parsedAllowedRolesSource = current;
    if (current.isBlank()) {
      allowedRolesCache = Set.of();
      unknownAllowedRoles = List.of();
      return;
    }
    Set<AriaRole> known = new LinkedHashSet<>();
    List<String> unknown = new ArrayList<>();
    for (String raw : trimSplitCommaSeparatedList(current)) {
      String normalized = raw.toLowerCase(Locale.ROOT);
      AriaRole role = AriaRole.of(normalized);
      if (role == null) {
        unknown.add(raw);
      } else {
        known.add(role);
      }
    }
    allowedRolesCache = known.isEmpty() ? Set.of() : Collections.unmodifiableSet(known);
    if (!unknown.isEmpty()) {
      unknownAllowedRoles = Collections.unmodifiableList(unknown);
      LOG.warn(String.format(UNKNOWN_ALLOWED_ROLE_WARNING, String.join(", ", unknown)));
    } else {
      unknownAllowedRoles = List.of();
    }
  }
}
