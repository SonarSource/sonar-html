/*
 * SonarQube HTML
 * Copyright (C) 2010-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource Sàrl.
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

@Rule(key = "PageWithoutFaviconCheck")
public class PageWithoutFaviconCheck extends AbstractPageCheck {

  private int currentHeadTagLine;
  private boolean foundTitleTag;

  @Override
  public void startDocument(List<Node> nodes) {
    currentHeadTagLine = 0;
  }

  @Override
  public void startElement(TagNode node) {
    if (isHeadTag(node)) {
      currentHeadTagLine = node.getStartLinePosition();
      foundTitleTag = false;
    } else if (currentHeadTagLine != 0 && isFaviconTag(node)) {
      foundTitleTag = true;
    }
  }

  @Override
  public void endElement(TagNode node) {
    int line = 0;

    if (isHeadTag(node)) {
      line = currentHeadTagLine;
      currentHeadTagLine = 0;
    }

    if (!foundTitleTag && line != 0) {
      createViolation(line, "Add a 'favicon' declaration in this 'header' tag.");
    }
  }

  private static boolean isHeadTag(TagNode node) {
    return "HEAD".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isFaviconTag(TagNode node) {
    String rel = node.getPropertyValue("REL");

    return isLinkTag(node) &&
      rel != null &&
      ("ICON".equalsIgnoreCase(rel) || "SHORTCUT ICON".equalsIgnoreCase(rel));
  }

  private static boolean isLinkTag(TagNode node) {
    return "LINK".equalsIgnoreCase(node.getNodeName());
  }

}
