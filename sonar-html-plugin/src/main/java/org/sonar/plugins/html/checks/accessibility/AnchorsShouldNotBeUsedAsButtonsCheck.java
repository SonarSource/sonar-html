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

import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;

import java.util.regex.Pattern;

@Rule(key = "S6844")
public class AnchorsShouldNotBeUsedAsButtonsCheck extends AbstractPageCheck {
  static boolean isAJavascriptHandler(String value) {
    String pattern = "^\\W*?javascript:.*";

    return Pattern.matches(pattern, value);
  }

  private static boolean hasButtonRole(TagNode node) {
    return "button".equalsIgnoreCase(node.getPropertyValue("role"));
  }

  private static boolean hasKeyboardHandler(TagNode node) {
    return hasEventHandler(node, "keydown") ||
      hasEventHandler(node, "keyup") ||
      hasEventHandler(node, "keypress");
  }

  private static boolean hasEventHandler(TagNode node, String eventName) {
    // Standard HTML: onkeydown, onkeyup, onkeypress
    // Angular: (keydown), on-keydown, ng-keydown
    // Vue: v-on:keydown (shorthand @keydown has @ stripped by parser, leaving bare "keydown")
    return node.getAttribute("on" + eventName) != null
      || node.getAttribute("(" + eventName + ")") != null
      || node.getAttribute("on-" + eventName) != null
      || node.getAttribute("ng-" + eventName) != null
      || node.getAttribute("v-on:" + eventName) != null
      || node.getAttribute(eventName) != null;
  }

  @Override
  public void startElement(TagNode node) {
    if (!"a".equalsIgnoreCase(node.getNodeName())) {
      return;
    }
    String onClickAttribute = node.getAttribute("onclick");
    if (onClickAttribute == null) {
      return;
    }
    String hrefAttribute = node.getAttribute("href");
    boolean hasInvalidHref = hrefAttribute == null || hrefAttribute.isBlank() || "#".equals(hrefAttribute) || isAJavascriptHandler(hrefAttribute);

    if (!hasInvalidHref) {
      return;
    }

    if (hasButtonRole(node)) {
      if (!hasKeyboardHandler(node)) {
        createViolation(node, "Anchor tags with role=\"button\" must also handle keyboard events for accessibility.");
      }
    } else {
      createViolation(node, "Anchor tags should not be used as buttons.");
    }
  }
}
