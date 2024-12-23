/*
 * SonarSource HTML analyzer :: Sonar Plugin
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
import org.sonar.plugins.html.api.Helpers;
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
    if (role == null || Helpers.isDynamicValue(role)) {
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
