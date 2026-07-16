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

import static org.sonar.plugins.html.api.HtmlConstants.INTERACTIVE_ROLES;
import static org.sonar.plugins.html.api.HtmlConstants.hasKnownHTMLTag;
import static org.sonar.plugins.html.api.HtmlConstants.isInteractiveElement;

import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "S6845")
public class NoNoninteractiveTabIndexCheck extends AbstractPageCheck {

  private static final String MESSAGE = "\"tabindex\" should only be declared on interactive elements.";

  @Override
  public void startElement(TagNode node) {
    if (!hasKnownHTMLTag(node) || isInteractiveElement(node) || hasAllowedRole(node)) {
      return;
    }

    var tabIndex = node.getPropertyValue("tabindex");
    if (tabIndex == null) {
      return;
    }

    try {
      var tabIndexValue = Integer.parseInt(tabIndex);
      if (tabIndexValue >= 0) {
        createViolation(node, MESSAGE);
      }
    } catch (NumberFormatException e) {
      // ignore
    }
  }

  /**
   * Returns whether the element declares a role that allows a non-negative tabindex.
   *
   * @param node the element whose role declaration is inspected
   * @return true when the element declares an interactive role or the tabpanel role
   */
  private static boolean hasAllowedRole(TagNode node) {
    var role = resolveStaticRole(node);
    if (role == null) {
      return false;
    }

    return "tabpanel".equalsIgnoreCase(role)
      || INTERACTIVE_ROLES.stream().anyMatch(role::equalsIgnoreCase);
  }

  private static String resolveStaticRole(TagNode node) {
    var roleAttribute = node.getProperty("role");
    if (roleAttribute == null) {
      return null;
    }

    var roleValue = roleAttribute.getValue();
    if (roleAttribute.getName().equalsIgnoreCase("role")) {
      var normalizedRole = roleValue.trim();
      return normalizedRole.isEmpty() ? null : normalizedRole;
    }

    // Bound role values are only trusted when they are explicit string literals.
    if (!isStaticStringLiteral(roleValue)) {
      return null;
    }

    var normalizedRole = roleValue.substring(1, roleValue.length() - 1).trim();
    return normalizedRole.isEmpty() ? null : normalizedRole;
  }

  private static boolean isStaticStringLiteral(String value) {
    if (value == null || value.length() < 2) {
      return false;
    }

    char first = value.charAt(0);
    char last = value.charAt(value.length() - 1);
    return (first == '\'' && last == '\'') || (first == '"' && last == '"');
  }
}
