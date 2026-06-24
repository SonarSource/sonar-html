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

import java.util.Set;
import org.sonar.plugins.html.api.Thymeleaf;
import org.sonar.plugins.html.node.TagNode;

import static org.sonar.plugins.html.api.HtmlConstants.isInteractiveElement;

public class AccessibilityUtils {

  /**
   * Attributes that template engines use to inject text content into an element at render time:
   * Thymeleaf {@code th:text}/{@code th:utext} and Vue {@code v-text}/{@code v-html}. Centralized
   * here so accessibility checks that ask "does this element get its text from a template?" all
   * see the same definition.
   */
  public static final Set<String> TEMPLATE_TEXT_ATTRIBUTES = Set.of("th:text", "th:utext", "v-text", "v-html");

  private AccessibilityUtils() {
    // utility class
  }

  /**
   * Returns whether {@code element} carries any template-text attribute with a usable value.
   */
  public static boolean hasNonEmptyTemplateTextAttribute(TagNode element) {
    for (String attributeName : TEMPLATE_TEXT_ATTRIBUTES) {
      if (!Thymeleaf.isEmptyValue(element.getAttribute(attributeName))) {
        return true;
      }
    }
    return false;
  }

  public static boolean isHiddenFromScreenReader(TagNode element) {
    return (
      (
        "input".equalsIgnoreCase(element.getNodeName()) &&
          "hidden".equalsIgnoreCase(element.getPropertyValue("type"))
      ) ||
        "true".equalsIgnoreCase(element.getPropertyValue("aria-hidden"))
    );
  }

  public static boolean isDisabledElement(TagNode element) {
    var disabledAttr = element.getAttribute("disabled");
    if (disabledAttr != null) {
      return true;
    }

    var ariaDisabledAttr = element.getAttribute("aria-disabled");
    return "true".equalsIgnoreCase(ariaDisabledAttr);
  }

  public static boolean isFocusableElement(TagNode element) {
    String tabindex = element.getPropertyValue("tabindex");
    try {
      if (isInteractiveElement(element)) {
        return tabindex == null || Double.parseDouble(tabindex) >= 0;
      }
      return tabindex != null && Double.parseDouble(tabindex) >= 0;
    } catch (NumberFormatException e) {
      // if it's declaratively set (i.e., angular or php), we assume it can be positive
      return true;
    }
  }
}
