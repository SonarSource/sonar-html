/*
 * SonarQube HTML
 * Copyright (C) SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * You can redistribute and/or modify this program under the terms of
 * the Sonar Source-Available License Version 1, as published by SonarSource Sàrl.
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
    if (isFrame(node) && !node.hasProperty("TITLE") && !isHidden(node)) {
      createViolation(node, "Add a \"title\" attribute to this <" + node.getNodeName() + "> tag.");
    }
  }

  private static boolean isFrame(TagNode node) {
    return "FRAME".equalsIgnoreCase(node.getNodeName()) ||
      "IFRAME".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isHidden(TagNode node) {
    return node.hasAttribute("hidden") ||
      "true".equalsIgnoreCase(node.getPropertyValue("aria-hidden"));
  }

}
