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
import java.util.Map;
import java.util.Set;
import org.sonar.check.Rule;
import org.sonar.plugins.html.api.accessibility.AriaRole;
import org.sonar.plugins.html.api.accessibility.Element;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "S6842")
public class NoNoninteractiveElementToInteractiveRoleCheck extends AbstractPageCheck {

  private static final String MESSAGE = "Non-interactive elements should not be assigned interactive roles.";
  private static final Map<Element, Set<AriaRole>> ALLOWED_COMPOSITE_WIDGET_OVERRIDES = Map.of(
    Element.FIELDSET, EnumSet.of(AriaRole.RADIOGROUP),
    Element.LI, EnumSet.of(
      AriaRole.MENUITEM,
      AriaRole.MENUITEMCHECKBOX,
      AriaRole.MENUITEMRADIO,
      AriaRole.OPTION,
      AriaRole.ROW,
      AriaRole.TAB,
      AriaRole.TREEITEM),
    Element.OL, EnumSet.of(
      AriaRole.LISTBOX,
      AriaRole.MENU,
      AriaRole.MENUBAR,
      AriaRole.RADIOGROUP,
      AriaRole.TABLIST,
      AriaRole.TREE,
      AriaRole.TREEGRID),
    Element.TABLE, EnumSet.of(AriaRole.GRID),
    Element.UL, EnumSet.of(
      AriaRole.LISTBOX,
      AriaRole.MENU,
      AriaRole.MENUBAR,
      AriaRole.RADIOGROUP,
      AriaRole.TABLIST,
      AriaRole.TREE,
      AriaRole.TREEGRID)
  );

  @Override
  public void startElement(TagNode node) {
    if (
      hasKnownHTMLTag(node) &&
      isNonInteractiveElement(node) &&
      hasInteractiveRole(node) &&
      !isAllowedCompositeWidgetOverride(node)
    ) {
      createViolation(node, MESSAGE);
    }
  }

  /**
   * Checks whether the element/role pair is a supported composite-widget override.
   * @param node the element being analyzed
   * @return true when the role is allowed for that element by the composite-widget allowlist
   */
  private static boolean isAllowedCompositeWidgetOverride(TagNode node) {
    var element = Element.of(node.getNodeName().toLowerCase(Locale.ROOT));
    var roleAttribute = node.getAttribute("role");
    if (element == null || roleAttribute == null) {
      return false;
    }
    var role = AriaRole.of(roleAttribute.toLowerCase(Locale.ROOT));
    return role != null && ALLOWED_COMPOSITE_WIDGET_OVERRIDES.getOrDefault(element, Set.of()).contains(role);
  }

}
