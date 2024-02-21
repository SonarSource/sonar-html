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

import static org.sonar.plugins.html.api.HtmlConstants.INTERACTIVE_ELEMENTS;
import static org.sonar.plugins.html.api.HtmlConstants.INTERACTIVE_ROLES;
import static org.sonar.plugins.html.api.HtmlConstants.KNOWN_HTML_TAGS;

import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "S6845")
public class NoNoninteractiveTabIndexCheck extends AbstractPageCheck {

  private static final String MESSAGE = "\"tabIndex\" should only be declared on interactive elements.";

  @Override
  public void startElement(TagNode node) {
    if (!isHTMLTag(node) || isInteractiveElement(node) || hasInteractiveRole(node)) {
      return;
    }

    var tabIndex = node.getAttribute("tabindex");
    if (tabIndex == null) {
      return;
    }

    try {
      var tabIndexValue = Integer.parseInt(tabIndex);
      if (tabIndexValue >= 0) {
        createViolation(node, MESSAGE);
      }
    } catch (NumberFormatException e) {
      // ignore
    }
  }

  private static boolean isHTMLTag(TagNode element) {
    var tagName = element.getNodeName();
    return KNOWN_HTML_TAGS.stream().anyMatch(tagName::equalsIgnoreCase);
  }

  private static boolean isInteractiveElement(TagNode element) {
    var tagName = element.getNodeName();
    return INTERACTIVE_ELEMENTS.stream().anyMatch(tagName::equalsIgnoreCase);
  }

  private static boolean hasInteractiveRole(TagNode element) {
    var role = element.getAttribute("role");
    return role != null && INTERACTIVE_ROLES.stream().anyMatch(role::equalsIgnoreCase);
  }
}
