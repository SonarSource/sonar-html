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
package org.sonar.plugins.html.checks.sonar;

import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;

import java.util.List;

@Rule(key = "NonConsecutiveHeadingCheck")
public class NonConsecutiveHeadingCheck extends AbstractPageCheck {

  private final int[] firstUsage = new int[6];

  @Override
  public void startDocument(List<Node> nodes) {
    for (int i = 0; i < firstUsage.length; i++) {
      firstUsage[i] = 0;
    }
  }

  @Override
  public void startElement(TagNode node) {
    if (isHeadingTag(node)) {
      int index = node.getNodeName().charAt(1) - '1';

      if (firstUsage[index] == 0) {
        firstUsage[index] = node.getStartLinePosition();
      }
    }
  }

  @Override
  public void endDocument() {
    for (int i = firstUsage.length - 1; i > 0; i--) {
      if (firstUsage[i] != 0 && firstUsage[i - 1] == 0) {
        createViolation(firstUsage[i], "Do not skip level H" + i + ".");
      }
    }
  }

  private static boolean isHeadingTag(TagNode node) {
    return node.getNodeName().length() == 2 &&
      Character.toUpperCase(node.getNodeName().charAt(0)) == 'H' &&
      node.getNodeName().charAt(1) >= '1' &&
      node.getNodeName().charAt(1) <= '6';
  }

}
