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

import static org.sonar.plugins.html.api.HtmlConstants.hasInteractiveRole;
import static org.sonar.plugins.html.api.HtmlConstants.hasKnownHTMLTag;
import static org.sonar.plugins.html.api.HtmlConstants.isNonInteractiveElement;

import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;
import org.sonar.check.Rule;
import org.sonar.plugins.html.api.accessibility.AriaRole;
import org.sonar.plugins.html.api.accessibility.Element;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "S6842")
public class NoNoninteractiveElementToInteractiveRoleCheck extends AbstractPageCheck {

  private static final String MESSAGE = "Non-interactive elements should not be assigned interactive roles.";
  private static final Set<Element> ELEMENTS_WITH_ANY_INTERACTIVE_ROLE = EnumSet.of(
    Element.TABLE,
    Element.TBODY,
    Element.TFOOT,
    Element.THEAD
  );
  private static final Set<Element> LIST_CONTAINER_ELEMENTS = EnumSet.of(
    Element.MENU,
    Element.OL,
    Element.UL
  );
  private static final Set<AriaRole> ALLOWED_INTERACTIVE_ROLES_FOR_LIST_CONTAINERS = EnumSet.of(
    AriaRole.LISTBOX,
    AriaRole.MENU,
    AriaRole.MENUBAR,
    AriaRole.RADIOGROUP,
    AriaRole.TABLIST,
    AriaRole.TREE
  );
  private static final Set<String> LIST_CONTAINER_ROLES_WITHOUT_LIST_SEMANTICS = Set.of(
    "directory",
    "group",
    "listbox",
    "menu",
    "menubar",
    "none",
    "presentation",
    "radiogroup",
    "tablist",
    "toolbar",
    "tree"
  );

  @Override
  public void startElement(TagNode node) {
    if (
      hasKnownHTMLTag(node) &&
      isNonInteractiveElement(node) &&
      hasInteractiveRole(node) &&
      !isAllowedByAriaInHtmlSpec(node)
    ) {
      createViolation(node, MESSAGE);
    }
  }

  /**
   * Checks whether the element/role pair is allowed by the ARIA in HTML conformance table.
   * @param node the element being analyzed
   * @return true when the role is allowed for that element by the ARIA in HTML specification
   */
  private static boolean isAllowedByAriaInHtmlSpec(TagNode node) {
    var element = Element.of(node.getNodeName().toLowerCase(Locale.ROOT));
    var role = interactiveRole(node);
    if (element == null || role == null) {
      return false;
    }

    if (ELEMENTS_WITH_ANY_INTERACTIVE_ROLE.contains(element)) {
      return true;
    }

    return switch (element) {
      case FIELDSET -> role == AriaRole.RADIOGROUP;
      case MENU, OL, UL -> ALLOWED_INTERACTIVE_ROLES_FOR_LIST_CONTAINERS.contains(role);
      case LI -> isListItemRoleAllowed(node);
      default -> false;
    };
  }

  private static boolean isListItemRoleAllowed(TagNode node) {
    var parent = node.getParent();
    if (parent == null) {
      return false;
    }

    var parentElement = Element.of(parent.getNodeName().toLowerCase(Locale.ROOT));
    var parentRoleAttribute = roleAttribute(parent);
    return parentElement != null
      && LIST_CONTAINER_ELEMENTS.contains(parentElement)
      && parentRoleAttribute != null
      && LIST_CONTAINER_ROLES_WITHOUT_LIST_SEMANTICS.contains(parentRoleAttribute);
  }

  private static AriaRole interactiveRole(TagNode node) {
    var role = role(node);
    return role != null && AriaRole.LIST != role ? role : null;
  }

  private static AriaRole role(TagNode node) {
    var roleAttribute = roleAttribute(node);
    return roleAttribute == null ? null : AriaRole.of(roleAttribute);
  }

  private static String roleAttribute(TagNode node) {
    var roleAttribute = node.getAttribute("role");
    return roleAttribute == null ? null : roleAttribute.toLowerCase(Locale.ROOT);
  }

}
