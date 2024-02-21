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

import static org.sonar.plugins.html.api.HtmlConstants.ABSTRACT_ROLES;
import static org.sonar.plugins.html.api.HtmlConstants.INTERACTIVE_ELEMENTS;
import static org.sonar.plugins.html.api.HtmlConstants.INTERACTIVE_ROLES;
import static org.sonar.plugins.html.api.HtmlConstants.KNOWN_HTML_TAGS;
import static org.sonar.plugins.html.api.HtmlConstants.NON_INTERACTIVE_ELEMENTS;
import static org.sonar.plugins.html.api.HtmlConstants.NON_INTERACTIVE_ROLES;
import static org.sonar.plugins.html.api.HtmlConstants.PRESENTATION_ROLES;
import static org.sonar.plugins.html.checks.accessibility.AccessibilityUtils.isHiddenFromScreenReader;

import java.util.HashSet;
import java.util.Set;
import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "S6847")
public class NoNonInteractiveElementsWithHandlersCheck extends AbstractPageCheck {

  private static final String MESSAGE = "Non-interactive elements should not be assigned mouse or keyboard event listeners.";
  private static final Set<String> INTERACTIVE_PROPS = new HashSet<>();

  static {
    INTERACTIVE_PROPS.addAll(EventHandlers.EVENT_HANDLERS_BY_TYPE.get("focus"));
    INTERACTIVE_PROPS.addAll(EventHandlers.EVENT_HANDLERS_BY_TYPE.get("image"));
    INTERACTIVE_PROPS.addAll(EventHandlers.EVENT_HANDLERS_BY_TYPE.get("keyboard"));
    INTERACTIVE_PROPS.addAll(EventHandlers.EVENT_HANDLERS_BY_TYPE.get("mouse"));
  }

  @Override
  public void startElement(TagNode element) {
    var tagName = element.getNodeName();
    if (!KNOWN_HTML_TAGS.contains(tagName)) {
      return;
    }

    if (!hasInteractiveProps(element)
        || isContentEditable(element)
        || isHiddenFromScreenReader(element)
        || isPresentationRole(element)
        || isInteractiveElement(element)
        || isInteractiveRole(element)
        || (!isNonInteractiveElement(element) && !isNonInteractiveRole(element))
        || isAbstractRole(element)) {
      return;
    }

    createViolation(element.getStartLinePosition(), MESSAGE);
  }

  private static boolean hasInteractiveProps(TagNode element) {
    return INTERACTIVE_PROPS.stream().anyMatch(prop -> {
      var attr = element.getAttribute(prop);
      return attr != null && !attr.isEmpty();
    });
  }

  private static boolean isContentEditable(TagNode element) {
    var contentEditable = element.getAttribute("contenteditable");
    return contentEditable != null && contentEditable.equalsIgnoreCase("true");
  }

  private static boolean isInteractiveElement(TagNode element) {
    var tagName = element.getNodeName();
    return INTERACTIVE_ELEMENTS.stream().anyMatch(tagName::equalsIgnoreCase);
  }

  private static boolean isNonInteractiveElement(TagNode element) {
    var tagName = element.getNodeName();
    return NON_INTERACTIVE_ELEMENTS.stream().anyMatch(tagName::equalsIgnoreCase);
  }

  private static boolean isInteractiveRole(TagNode element) {
    var role = element.getAttribute("role");
    return role != null && INTERACTIVE_ROLES.stream().anyMatch(role::equalsIgnoreCase);
  }

  private static boolean isNonInteractiveRole(TagNode element) {
    var role = element.getAttribute("role");
    return role != null && NON_INTERACTIVE_ROLES.stream().anyMatch(role::equalsIgnoreCase);
  }

  private static boolean isPresentationRole(TagNode element) {
    var role = element.getAttribute("role");
    return role != null && PRESENTATION_ROLES.stream().anyMatch(role::equalsIgnoreCase);
  }

  private static boolean isAbstractRole(TagNode element) {
    var role = element.getAttribute("role");
    return role != null && ABSTRACT_ROLES.stream().anyMatch(role::equalsIgnoreCase);
  }
}