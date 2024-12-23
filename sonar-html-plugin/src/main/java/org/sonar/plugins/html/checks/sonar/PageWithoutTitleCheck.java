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
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;

import java.util.List;

@Rule(key = "PageWithoutTitleCheck")
public class PageWithoutTitleCheck extends AbstractPageCheck {

  private int currentHtmlTagLine;
  private int currentHeadTagLine;
  private boolean foundTitleTag;
  private boolean isReported;

  @Override
  public void startDocument(List<Node> nodes) {
    currentHtmlTagLine = 0;
    currentHeadTagLine = 0;
  }

  @Override
  public void startElement(TagNode node) {
    if (isHtmlTag(node)) {
      currentHtmlTagLine = node.getStartLinePosition();
      isReported = false;
      foundTitleTag = false;
    } else if (isHeadTag(node)) {
      currentHeadTagLine = node.getStartLinePosition();
      isReported = false;
      foundTitleTag = false;
    } else if (currentHeadTagLine != 0 && isTitleTag(node)) {
      foundTitleTag = true;
    }
  }

  @Override
  public void endElement(TagNode node) {
    int line = 0;

    if (isHtmlTag(node)) {
      line = currentHtmlTagLine;
      currentHtmlTagLine = 0;
      currentHeadTagLine = 0;
    } else if (isHeadTag(node)) {
      line = currentHeadTagLine;
      currentHeadTagLine = 0;
    }

    if (!foundTitleTag && line != 0 && !isReported) {
      createViolation(line, "Add a <title> tag to this page.");
      isReported = true;
    }
  }

  private static boolean isHtmlTag(TagNode node) {
    return "HTML".equalsIgnoreCase(node.getLocalName());
  }

  private static boolean isHeadTag(TagNode node) {
    return "HEAD".equalsIgnoreCase(node.getLocalName());
  }

  private static boolean isTitleTag(TagNode node) {
    return "TITLE".equalsIgnoreCase(node.getLocalName());
  }

}
