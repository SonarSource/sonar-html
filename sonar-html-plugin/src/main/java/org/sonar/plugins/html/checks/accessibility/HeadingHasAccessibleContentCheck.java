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

import org.sonar.check.Rule;
import org.sonar.plugins.html.api.Helpers;
import org.sonar.plugins.html.api.BufferStack;
import org.sonar.plugins.html.api.HtmlConstants;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.DirectiveNode;
import org.sonar.plugins.html.node.ExpressionNode;
import org.sonar.plugins.html.node.TagNode;
import org.sonar.plugins.html.node.TextNode;
import org.sonar.plugins.html.node.Attribute;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

@Rule(key = "S6850")
public class HeadingHasAccessibleContentCheck extends AbstractPageCheck {
  private final List<String> invalidAttributes = List.of(
    "aria-hidden"
  );

  private final List<String> vueJsContentLikeAttributes = List.of(
    "v-html",
    "v-text"
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
      if (!HtmlConstants.KNOWN_HTML_TAGS.contains(nodeName)) {
        bufferStack.write(nodeName);
      }
    }

    // vueJS attributes that maps to content are considered as content
    vueJsContentLikeAttributes.forEach(attributeName -> {
      String nodeAttribute = node.getAttribute(attributeName);

      if (nodeAttribute != null && !nodeAttribute.isBlank() && bufferStack.getLevel() > 0) {
        bufferStack.write(nodeAttribute);
      }
    });

    super.startElement(node);
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

    super.endElement(node);
  }

  @Override
  public void endDocument() {
    openingHeadingTags.clear();

    super.endDocument();
  }

  @Override
  public void characters(TextNode textNode) {
    if (bufferStack.getLevel() > 0) {
      bufferStack.write(textNode.toString());
    }

    super.characters(textNode);
  }

  @Override
  public void expression(ExpressionNode expressionNode) {
    if (bufferStack.getLevel() > 0) {
      bufferStack.write(expressionNode.toString());
    }

    super.expression(expressionNode);
  }

  @Override
  public void directive(DirectiveNode directiveNode) {
    if (bufferStack.getLevel() > 0) {
      bufferStack.write(directiveNode.toString());
    }

    super.directive(directiveNode);
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
