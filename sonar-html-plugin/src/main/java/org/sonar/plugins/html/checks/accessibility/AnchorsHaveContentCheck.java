/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (c) 2010-2024 SonarSource SA and Matthijs Galesloot
 * sonarqube@googlegroups.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

import static org.sonar.plugins.html.checks.accessibility.AccessibilityUtils.isHiddenFromScreenReader;

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
    return element.hasProperty("title") || element.hasProperty("aria-label");
  }
}
