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
import static org.sonar.plugins.html.api.HtmlConstants.isAbstractRole;
import static org.sonar.plugins.html.api.HtmlConstants.isInteractiveElement;
import static org.sonar.plugins.html.api.HtmlConstants.isInteractiveRole;
import static org.sonar.plugins.html.api.HtmlConstants.isNonInteractiveElement;
import static org.sonar.plugins.html.api.HtmlConstants.isNonInteractiveRole;
import static org.sonar.plugins.html.api.HtmlConstants.isPresentationRole;
import static org.sonar.plugins.html.checks.accessibility.AccessibilityUtils.isHiddenFromScreenReader;

import java.util.HashSet;
import java.util.Set;
import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "S6848")
public class NoStaticElementInteractionsCheck extends AbstractPageCheck {

  private static final String MESSAGE = "Avoid non-native interactive elements. If using native HTML is not possible," +
    " add an appropriate role and support for tabbing, mouse, keyboard, and touch inputs to an interactive content element.";
  private static final Set<String> INTERACTIVE_PROPS = new HashSet<>();

  static {
    INTERACTIVE_PROPS.addAll(EventHandlers.EVENT_HANDLERS_BY_TYPE.get("focus"));
    INTERACTIVE_PROPS.addAll(EventHandlers.EVENT_HANDLERS_BY_TYPE.get("keyboard"));
    INTERACTIVE_PROPS.addAll(EventHandlers.EVENT_HANDLERS_BY_TYPE.get("mouse"));
  }

  @Override
  public void startElement(TagNode element) {
    var tagName = element.getNodeName();

    if (!KNOWN_HTML_TAGS.contains(tagName)) {
      return;
    }

    if (
      !hasInteractiveProps(element) ||
      isHiddenFromScreenReader(element) ||
      isPresentationRole(element) ||
      isInteractiveElement(element) ||
      isInteractiveRole(element) ||
      isNonInteractiveElement(element) ||
      isNonInteractiveRole(element) ||
      isAbstractRole(element)
    ) {
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
}