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
package org.sonar.plugins.html.checks.sonar;

import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "FrameWithoutTitleCheck")
public class FrameWithoutTitleCheck extends AbstractPageCheck {

  @Override
  public void startElement(TagNode node) {
    if (isFrame(node) && !node.hasProperty("TITLE")) {
      createViolation(node, "Add a \"title\" attribute to this <" + node.getNodeName() + "> tag.");
    }
  }

  private static boolean isFrame(TagNode node) {
    return "FRAME".equalsIgnoreCase(node.getNodeName()) ||
      "IFRAME".equalsIgnoreCase(node.getNodeName());
  }

}
