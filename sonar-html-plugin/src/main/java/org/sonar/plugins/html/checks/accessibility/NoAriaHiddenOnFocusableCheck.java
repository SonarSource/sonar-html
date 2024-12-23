/*
 * SonarQube HTML Plugin :: Sonar Plugin
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
