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

@Rule(key = "WmodeIsWindowCheck")
public class WmodeIsWindowCheck extends AbstractPageCheck {

  private static final String WMODE = "WMODE";

  @Override
  public void startElement(TagNode node) {
    int line = 0;

    if (isParam(node) && hasInvalidObjectWmodeParam(node) && node.getParent() != null && FlashHelper.isFlashObject(node.getParent())) {
      line = node.getStartLinePosition();
    } else if (isEmbed(node) && hasInvalidEmbedWmodeAttribute(node) && FlashHelper.isFlashEmbed(node)) {
      line = getWmodeAttributeLine(node);
    }

    if (line != 0) {
      createViolation(line, "Set the value of the 'wmode' parameter to 'window'.");
    }
  }

  private static int getWmodeAttributeLine(TagNode node) {
    return node.getProperty(WMODE).getLine();
  }

  private static boolean isParam(TagNode node) {
    return "PARAM".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean hasInvalidObjectWmodeParam(TagNode node) {
    String name = node.getPropertyValue("name");
    String value = node.getPropertyValue("value");

    return name != null &&
      value != null &&
      WMODE.equalsIgnoreCase(name) &&
      !"WINDOW".equalsIgnoreCase(value);
  }

  private static boolean isEmbed(TagNode node) {
    return "EMBED".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean hasInvalidEmbedWmodeAttribute(TagNode node) {
    String wmode = node.getPropertyValue(WmodeIsWindowCheck.WMODE);

    return wmode != null &&
      !"WINDOW".equalsIgnoreCase(wmode);
  }

}
