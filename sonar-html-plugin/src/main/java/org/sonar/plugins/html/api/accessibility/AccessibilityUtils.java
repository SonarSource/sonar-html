/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (c) 2010-2024 SonarSource SA and Matthijs Galesloot
 * sonarqube@googlegroups.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
