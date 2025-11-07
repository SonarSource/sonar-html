/*
 * SonarQube HTML
 * Copyright (C) 2010-2025 SonarSource Sàrl
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
import org.sonar.plugins.html.api.HtmlConstants;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "S6823")
public class AriaActiveDescendantHasTabIndexCheck extends AbstractPageCheck {
  @Override
  public void startElement(TagNode node) {
    var ariaActiveDescendant = node.getAttribute("aria-activedescendant");

    if (ariaActiveDescendant != null) {
      var tabIndex = node.getAttribute("tabindex");

      if ((tabIndex == null || tabIndex.isBlank()) && !HtmlConstants.isInteractiveElement(node)) {
        createViolation(node, "An element that manages focus with `aria-activedescendant` must have a tabindex.");
      }
    }
  }
}
