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

import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;
import org.sonar.plugins.html.node.TextNode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Rule(key="S7927")
public class AccessibleNameMatchesLabelCheck extends AbstractPageCheck {
  public static final String MESSAGE = "The accessible name should be part of the visible label.";
  private static final Set<String> INTERACTIVE_TAGS = new HashSet<>(Arrays.asList(
          "button", "a", "textarea", "select"
  ));

  public void checkTagElement(TagNode node, String visibleLabel) {
    String tagName = node.getNodeName().toLowerCase();

    // Check if the element is interactive
    if (INTERACTIVE_TAGS.contains(tagName) || hasRoleButton(node)) {

      // Skip if the label is empty or only one word
      if (visibleLabel.split("\\s+").length == 1) {
        return;
      }

      String accessibleName = extractAccessibleName(node);
      if (accessibleName == null || accessibleName.isEmpty()) {
        return;
      }

      if (!normalize(accessibleName).contains(visibleLabel)) {
        createViolation(node, MESSAGE);
      }
    }
  }

  @Override
  public void characters(TextNode textNode) {
    if (!textNode.isBlank() && textNode.getParent() != null) {
      checkTagElement(textNode.getParent(), normalize(textNode.getCode()));
    }
  }

  private boolean hasRoleButton(TagNode node) {
    String role = node.getAttribute("role");
    return role != null && role.equalsIgnoreCase("button");
  }

  private String extractAccessibleName(TagNode node) {
    // aria-label
    String ariaLabel = node.getAttribute("aria-label");
    if (ariaLabel != null && !ariaLabel.trim().isEmpty()) {
      return ariaLabel.trim();
    }

    // Fallback: visible label
    return null;
  }

  private String normalize(String text) {
    return text.toLowerCase().replaceAll("\\s+", " ").trim();
  }
}
