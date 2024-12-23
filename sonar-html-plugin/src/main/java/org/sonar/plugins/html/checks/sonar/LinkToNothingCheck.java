/*
 * SonarQube HTML
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
package org.sonar.plugins.html.checks.sonar;

import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "LinkToNothingCheck")
public class LinkToNothingCheck extends AbstractPageCheck {

  @Override
  public void startElement(TagNode node) {
    if (isATag(node) && hasHrefToNothing(node)) {
      createViolation(node, "Give this link a valid reference or remove the reference.");
    }
  }

  private static boolean isATag(TagNode node) {
    return "A".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean hasHrefToNothing(TagNode node) {
    String href = node.getAttribute("href");

    return href != null &&
      isPoitingToNothing(href);
  }

  private static boolean isPoitingToNothing(String target) {
    return "#".equalsIgnoreCase(target) ||
      "JAVASCRIPT:VOID(0)".equalsIgnoreCase(target) ||
      "JAVASCRIPT:VOID(0);".equalsIgnoreCase(target);
  }

}
