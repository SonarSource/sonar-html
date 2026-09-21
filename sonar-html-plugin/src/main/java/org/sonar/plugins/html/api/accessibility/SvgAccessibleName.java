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
package org.sonar.plugins.html.api.accessibility;

import java.util.Locale;
import org.sonar.plugins.html.api.HtmlConstants;
import org.sonar.plugins.html.api.Helpers;
import org.sonar.plugins.html.node.Attribute;
import org.sonar.plugins.html.node.TagNode;

/**
 * Whether an inline {@code <svg>} is exempt from needing an accessible name because it is hidden
 * from assistive technology or is effectively decorative. Not knowing (an Angular/Vue binding
 * controls {@code aria-hidden}/{@code role}) is treated the same as being exempt, to avoid flagging
 * an SVG whose real, runtime value we cannot see statically.
 */
public final class SvgAccessibleName {

  private SvgAccessibleName() {
  }

  /**
   * Returns whether {@code svg} is hidden from assistive technology: {@code aria-hidden="true"} on
   * the element itself or on any ancestor, or {@code aria-hidden} bound via an Angular/Vue property
   * binding (on itself or an ancestor) whose runtime value cannot be determined statically.
   */
  public static boolean isHiddenFromAssistiveTech(TagNode svg) {
    return isHiddenOrHasBoundVisibility(svg) || Helpers.hasAncestorMatching(svg, SvgAccessibleName::isHiddenOrHasBoundVisibility);
  }

  private static boolean isHiddenOrHasBoundVisibility(TagNode node) {
    if (AccessibilityUtils.isHiddenFromScreenReader(node)) {
      return true;
    }
    Attribute ariaHidden = node.getProperty("aria-hidden");
    return ariaHidden != null && AccessibilityUtils.isBindingForm(ariaHidden, "aria-hidden");
  }

  /**
   * Returns whether {@code svg} has an effectively presentational/decorative role: the first valid,
   * non-abstract token of its {@code role} attribute's fallback list is {@code presentation} or
   * {@code none}, or {@code role} is bound via an Angular/Vue property binding whose runtime value
   * cannot be determined statically.
   */
  public static boolean hasEffectivelyPresentationalRole(TagNode svg) {
    Attribute roleProperty = svg.getProperty("role");
    if (roleProperty != null && AccessibilityUtils.isBindingForm(roleProperty, "role")) {
      return true;
    }
    String roleAttr = svg.getAttribute("role");
    if (roleAttr == null || roleAttr.isBlank()) {
      return false;
    }
    for (String token : roleAttr.trim().split("\\s+")) {
      String normalized = token.toLowerCase(Locale.ROOT);
      if (HtmlConstants.PRESENTATION_ROLES.contains(normalized)) {
        return true;
      }
      AriaRole role = AriaRole.of(normalized);
      if (role == null || HtmlConstants.isAbstractRole(role)) {
        continue;
      }
      // First valid, non-abstract, non-presentation role in the fallback list wins: not decorative.
      return false;
    }
    return false;
  }
}
