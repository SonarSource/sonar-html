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
package org.sonar.plugins.html.api.accessibility;

import org.sonar.plugins.html.node.TagNode;

import static org.sonar.plugins.html.api.HtmlConstants.isInteractiveElement;

public class AccessibilityUtils {

  private AccessibilityUtils() {
    // utility class
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
    String tabindex = element.getAttribute("tabindex");
    if (isInteractiveElement(element)) {
      return tabindex == null || Double.parseDouble(tabindex) >= 0;
    }
    return tabindex != null && Double.parseDouble(tabindex) >= 0;
  }
}
