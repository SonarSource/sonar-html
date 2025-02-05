/*
 * SonarQube HTML
 * Copyright (C) 2010-2025 SonarSource SA
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

import org.sonar.plugins.html.api.accessibility.AriaProperty;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;

import static org.sonar.plugins.html.api.HtmlConstants.isReservedNode;

import java.util.Locale;

import org.sonar.check.Rule;

@Rule(key = "S6824")
public class AriaUnsupportedElementsCheck extends AbstractPageCheck {
  @Override
  public void startElement(TagNode element) {
    // Following logic from:
    // https://github.com/jsx-eslint/eslint-plugin-jsx-a11y/blob/main/src/rules/aria-unsupported-elements.js
    if (!isReservedNode(element)) {
      return;
    }
    element.getAttributes().forEach(attr -> {
      var attrName = attr.getName().toLowerCase(Locale.ROOT);
      if (AriaProperty.of(attrName) != null || attrName.equals("role")) {
        createViolation(
            element,
            String.format("This element does not support ARIA roles, states and properties. Try removing the prop %s.",
                attr.getName()));
      }
    });
  }
}
