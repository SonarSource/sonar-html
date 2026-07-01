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
package org.sonar.plugins.html.checks.accessibility;

import org.sonar.check.Rule;
import org.sonar.plugins.html.api.Helpers;
import org.sonar.plugins.html.api.BufferStack;
import org.sonar.plugins.html.api.Thymeleaf;
import org.sonar.plugins.html.api.accessibility.AccessibilityUtils;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.DirectiveNode;
import org.sonar.plugins.html.node.ExpressionNode;
import org.sonar.plugins.html.node.TagNode;
import org.sonar.plugins.html.node.TextNode;
import org.sonar.plugins.html.node.Attribute;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import static org.sonar.plugins.html.api.HtmlConstants.hasKnownHTMLTag;

@Rule(key = "S6850")
public class HeadingHasAccessibleContentCheck extends AbstractPageCheck {
  private final List<String> invalidAttributes = List.of(
    "aria-hidden"
  );

  private final BufferStack bufferStack = new BufferStack();

  private final Deque<TagNode> openingHeadingTags = new ArrayDeque<>();

  @Override
  public void startElement(TagNode node) {
    if (Helpers.isHeadingTag(node)) {
      bufferStack.start();
      openingHeadingTags.push(node);

      if (hasAnInvalidAttribute(node)) {
        createViolation(node);
      }
    } else {
      String nodeName = node.getNodeName();

      // tags that are not part of the known HTML tags list are considered as content
      if (!hasKnownHTMLTag(node)) {
        bufferStack.write(nodeName);
      }
    }

    // template-text attributes (Thymeleaf th:text/th:utext, Vue v-text/v-html) are content
    AccessibilityUtils.TEMPLATE_TEXT_ATTRIBUTES.forEach(attributeName -> {
      String nodeAttribute = node.getAttribute(attributeName);

      if (!Thymeleaf.isEmptyValue(nodeAttribute) && bufferStack.getLevel() > 0) {
        bufferStack.write(nodeAttribute);
      }
    });
  }

  @Override
  public void endElement(TagNode node) {
    if (Helpers.isHeadingTag(node) && !openingHeadingTags.isEmpty()) {
      String content = bufferStack.getAndFlush();
      TagNode openingTag = openingHeadingTags.pop();

      if (content.isBlank()) {
        createViolation(openingTag);
      }
    }
  }

  @Override
  public void endDocument() {
    openingHeadingTags.clear();
  }

  @Override
  public void characters(TextNode textNode) {
    if (bufferStack.getLevel() > 0) {
      bufferStack.write(textNode.toString());
    }
  }

  @Override
  public void expression(ExpressionNode expressionNode) {
    if (bufferStack.getLevel() > 0) {
      bufferStack.write(expressionNode.toString());
    }
  }

  @Override
  public void directive(DirectiveNode directiveNode) {
    if (bufferStack.getLevel() > 0) {
      bufferStack.write(directiveNode.toString());
    }
  }

  private boolean hasAnInvalidAttribute(TagNode node) {
    return node.getAttributes().stream()
      .map(Attribute::getName)
      .anyMatch(invalidAttributes::contains);
  }

  private void createViolation(TagNode node) {
    super.createViolation(node.getStartLinePosition(), "Headings must have content and the content must be accessible by a screen reader.");
  }
}
