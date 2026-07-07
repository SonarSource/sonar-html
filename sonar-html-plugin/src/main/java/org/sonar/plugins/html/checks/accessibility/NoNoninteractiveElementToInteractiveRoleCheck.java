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

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.sonar.check.Rule;
import org.sonar.plugins.html.api.Helpers;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "S6842")
public class NoNoninteractiveElementToInteractiveRoleCheck extends AbstractPageCheck {

  private static final String MESSAGE = "Non-interactive elements should not be assigned interactive roles.";

  // Elements that ARIA in HTML permits to take any role.
  private static final Set<String> ANY_ROLE_ELEMENTS = Set.of(
    "abbr", "address", "blockquote", "code", "del", "dfn", "em", "ins", "mark", "output",
    "p", "pre", "ruby", "strong", "sub", "sup", "table", "tbody", "tfoot", "thead", "time");

  // Interactive roles ARIA in HTML permits per element (lowercased role names).
  private static final Set<String> LIST_CONTAINER_ROLES = Set.of(
    "listbox", "menu", "menubar", "radiogroup", "tablist", "tree");
  private static final Map<String, Set<String>> ALLOWED_INTERACTIVE_ROLES = Map.of(
    "fieldset", Set.of("radiogroup"),
    "menu", LIST_CONTAINER_ROLES,
    "nav", Set.of("menu", "menubar", "tablist"),
    "ol", LIST_CONTAINER_ROLES,
    "ul", LIST_CONTAINER_ROLES);

  // Interactive roles allowed on an <img> that exposes an accessible name.
  private static final Set<String> IMG_INTERACTIVE_ROLES = Set.of(
    "button", "checkbox", "link", "menuitem", "menuitemcheckbox", "menuitemradio", "option",
    "progressbar", "radio", "scrollbar", "slider", "switch", "tab", "treeitem");

  // Interactive roles allowed on heading elements (h1-h6).
  private static final Set<String> HEADING_INTERACTIVE_ROLES = Set.of("tab");

  private static final Set<String> LABELABLE_CONTROLS = Set.of(
    "button", "input", "meter", "output", "progress", "select", "textarea");

  private static final Set<String> LIST_CONTAINER_ELEMENTS = Set.of("ul", "ol", "menu");

  @Override
  public void startElement(TagNode node) {
    if (
      hasKnownHTMLTag(node) &&
      isNonInteractiveElement(node) &&
      hasInteractiveRole(node) &&
      !isRoleAllowedBySpec(node)
    ) {
      createViolation(node, MESSAGE);
    }
  }

  private static boolean isRoleAllowedBySpec(TagNode node) {
    var tag = node.getNodeName().toLowerCase(Locale.ROOT);
    var role = node.getAttribute("role").toLowerCase(Locale.ROOT);

    if (ANY_ROLE_ELEMENTS.contains(tag)) {
      return true;
    }
    // Elements whose allowed roles depend on their context.
    switch (tag) {
      case "li":
        return !parentExposesListRole(node);
      case "img":
        return hasAccessibleName(node) && IMG_INTERACTIVE_ROLES.contains(role);
      case "figure":
        return !hasFigcaptionChild(node);
      case "label":
        return !isAssociatedLabel(node);
      default:
        break;
    }
    // Elements with an enumerated allowlist of interactive roles.
    if (Helpers.isHeadingTag(node)) {
      return HEADING_INTERACTIVE_ROLES.contains(role);
    }
    return ALLOWED_INTERACTIVE_ROLES.getOrDefault(tag, Set.of()).contains(role);
  }

  // A list item is restricted to listitem only when its parent list still exposes the list role.
  private static boolean parentExposesListRole(TagNode node) {
    var parent = node.getParent();
    if (parent == null || !LIST_CONTAINER_ELEMENTS.contains(parent.getNodeName().toLowerCase(Locale.ROOT))) {
      return false;
    }
    var parentRole = parent.getAttribute("role");
    return parentRole == null || parentRole.equalsIgnoreCase("list");
  }

  // An img exposes an accessible name via non-empty alt, aria-label or aria-labelledby.
  private static boolean hasAccessibleName(TagNode node) {
    return hasNonEmptyAttribute(node, "alt")
      || hasNonEmptyAttribute(node, "aria-label")
      || hasNonEmptyAttribute(node, "aria-labelledby");
  }

  private static boolean hasNonEmptyAttribute(TagNode node, String name) {
    var value = node.getAttribute(name);
    return value != null && !value.isEmpty();
  }

  // A figure caption must be the first or last child of the figure per the HTML content model.
  private static boolean hasFigcaptionChild(TagNode node) {
    var children = node.getChildren();
    if (children.isEmpty()) {
      return false;
    }
    return isFigcaption(children.get(0)) || isFigcaption(children.get(children.size() - 1));
  }

  private static boolean isFigcaption(TagNode node) {
    return "figcaption".equalsIgnoreCase(node.getNodeName());
  }

  // A label is associated when it references a control via for/htmlFor or wraps a labelable control.
  private static boolean isAssociatedLabel(TagNode node) {
    return node.hasProperty("for") || node.hasProperty("htmlFor") || containsLabelableControl(node);
  }

  private static boolean containsLabelableControl(TagNode node) {
    for (var child : node.getChildren()) {
      if (LABELABLE_CONTROLS.contains(child.getNodeName().toLowerCase(Locale.ROOT)) || containsLabelableControl(child)) {
        return true;
      }
    }
    return false;
  }

}
