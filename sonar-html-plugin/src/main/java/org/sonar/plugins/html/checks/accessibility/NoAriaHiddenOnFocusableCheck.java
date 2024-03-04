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
package org.sonar.plugins.html.checks.accessibility;

import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;

import static org.sonar.plugins.html.api.HtmlConstants.hasKnownHTMLTag;
import static org.sonar.plugins.html.api.accessibility.AccessibilityUtils.isFocusableElement;
import static org.sonar.plugins.html.api.accessibility.AccessibilityUtils.isHiddenFromScreenReader;

@Rule(key = "S6825")
public class NoAriaHiddenOnFocusableCheck extends AbstractPageCheck {

  private static final String MESSAGE = "aria-hidden=\"true\" must not be set on focusable elements.";

  @Override
  public void startElement(TagNode node) {
    if (!hasKnownHTMLTag(node)) {
      return;
    }
    if (
        isFocusableElement(node) &&
        hasAriaHidden(node)
    ) {
      createViolation(node, MESSAGE);
    }
  }

  private static boolean hasAriaHidden(TagNode node) {
    return "true".equalsIgnoreCase(node.getPropertyValue("aria-hidden"));
  }

}
