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
package org.sonar.plugins.html.checks.sonar;

import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "BoldAndItalicTagsCheck")
public class BoldAndItalicTagsCheck extends AbstractPageCheck {

  @Override
  public void startElement(TagNode node) {
    if (isBold(node)) {
      createViolation(node, "Replace this <" + node.getNodeName() + "> tag by <strong>.");
    } else if (isItalicAndNotAriaHidden(node)) {
      createViolation(node, "Replace this <" + node.getNodeName() + "> tag by <em>.");
    }
  }

  private static boolean isBold(TagNode node) {
    return "B".equalsIgnoreCase(node.getNodeName());
  }

  /**
   * Check if node is an italic tag and attribute 'aria-hidden' is not true.
   * Rule should be relaxed in this case ; <a href="https://www.w3.org/WAI/GL/wiki/Using_aria-hidden%3Dtrue_on_an_icon_font_that_AT_should_ignore">icon font</a> usage.
   * @param node The current HTML start tag
   * @return true if violation should be applied, false otherwise
   */
  private static boolean isItalicAndNotAriaHidden(TagNode node) {
    return "I".equalsIgnoreCase(node.getNodeName()) && !"true".equalsIgnoreCase(node.getPropertyValue("aria-hidden"));
  }

}
