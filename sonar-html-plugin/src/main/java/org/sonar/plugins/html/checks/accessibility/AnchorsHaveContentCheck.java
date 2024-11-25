/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (C) 2010-2024 SonarSource SA and Matthijs Galesloot
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

import java.util.ArrayDeque;
import java.util.Deque;
import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.DirectiveNode;
import org.sonar.plugins.html.node.ExpressionNode;
import org.sonar.plugins.html.node.TagNode;
import org.sonar.plugins.html.node.TextNode;

import static org.sonar.plugins.html.api.accessibility.AccessibilityUtils.isHiddenFromScreenReader;

@Rule(key = "S6827")
public class AnchorsHaveContentCheck extends AbstractPageCheck {

  private static final String MESSAGE = "Anchors must have content and the content must be accessible by a screen reader.";

  private Deque<Anchor> anchors = new ArrayDeque<>();

  private static class Anchor {
    final TagNode node;
    boolean hasContent;

    private Anchor(TagNode node, boolean hasContent) {
      this.node = node;
      this.hasContent = hasContent;
    }
  }

  @Override
  public void startElement(TagNode element) {
    if (isAnchor(element)) {
      anchors.push(new Anchor(element, hasContent(element)));
    }
  }

  @Override
  public void endElement(TagNode element) {
    if (isAnchor(element) && !anchors.isEmpty()) {
      var anchor = anchors.pop();
      if (!anchor.hasContent) {
        createViolation(anchor.node.getStartLinePosition(), MESSAGE);
      }
    }
  }

  @Override
  public void endDocument() {
    anchors.clear();
  }

  @Override
  public void characters(TextNode node) {
    if (!anchors.isEmpty()) {
      var anchor = anchors.peek();
      anchor.hasContent = anchor.hasContent || !node.isBlank();
    }
  }

  @Override
  public void directive(DirectiveNode node) {
    if (!anchors.isEmpty()) {
      var anchor = anchors.peek();
      anchor.hasContent = anchor.hasContent || "?php".equals(node.getNodeName());
     }
  }

  @Override
  public void expression(ExpressionNode node) {
    if (!anchors.isEmpty()) {
      var anchor = anchors.peek();
      anchor.hasContent = true;
    }
  }

  private static boolean isAnchor(TagNode element) {
    return "a".equalsIgnoreCase(element.getNodeName());
  }

  private static boolean hasContent(TagNode element) {
    var children = element.getChildren();
    for (TagNode child : children) {
      if (!isHiddenFromScreenReader(child)) {
        return true;
      }
    }
    return element.hasProperty("title") || element.hasProperty("aria-label") || element.hasAttribute("th:text")
       || element.hasAttribute("th:utext");
  }
}
