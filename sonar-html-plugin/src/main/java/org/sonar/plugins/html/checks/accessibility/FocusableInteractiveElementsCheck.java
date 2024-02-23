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

import static org.sonar.plugins.html.api.HtmlConstants.hasInteractiveRole;
import static org.sonar.plugins.html.api.HtmlConstants.hasKnownHTMLTag;
import static org.sonar.plugins.html.api.HtmlConstants.isInteractiveElement;
import static org.sonar.plugins.html.api.HtmlConstants.isNonInteractiveElement;
import static org.sonar.plugins.html.api.HtmlConstants.hasNonInteractiveRole;
import static org.sonar.plugins.html.api.HtmlConstants.hasPresentationRole;
import static org.sonar.plugins.html.api.accessibility.AccessibilityUtils.isDisabledElement;
import static org.sonar.plugins.html.api.accessibility.AccessibilityUtils.isHiddenFromScreenReader;

import java.util.HashSet;
import java.util.Set;

import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "S6852")
public class FocusableInteractiveElementsCheck extends AbstractPageCheck {

  private static final String MESSAGE_TEMPLATE = "Elements with the \"%s\" interactive role must be focusable.";

  private static final Set<String> INTERACTIVE_PROPS = new HashSet<>();
  static {
    INTERACTIVE_PROPS.addAll(EventHandlers.EVENT_HANDLERS_BY_TYPE.get("keyboard"));
    INTERACTIVE_PROPS.addAll(EventHandlers.EVENT_HANDLERS_BY_TYPE.get("mouse"));
  }

  @Override
  public void startElement(TagNode element) {
    var role = element.getAttribute("role");
    if (role == null) {
      return;
    }

    if (!hasKnownHTMLTag(element)
      || !hasInteractiveProps(element)
      || !hasInteractiveRole(element)
      || isDisabledElement(element)
      || isHiddenFromScreenReader(element)
      || isInteractiveElement(element)
      || isNonInteractiveElement(element)
      || hasPresentationRole(element)
      || hasNonInteractiveRole(element)
      || element.hasProperty("tabindex")
      ) {
      return;
    }

    var message = String.format(MESSAGE_TEMPLATE, role);
    createViolation(element.getStartLinePosition(), message);
  }

  private static boolean hasInteractiveProps(TagNode element) {
    return INTERACTIVE_PROPS.stream().anyMatch(prop -> {
      var attr = element.getAttribute(prop);
      return attr != null && !attr.isEmpty();
    });
  }
}
