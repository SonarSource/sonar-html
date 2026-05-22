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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.DirectiveNode;
import org.sonar.plugins.html.node.ExpressionNode;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;
import org.sonar.plugins.html.node.TextNode;

import static org.sonar.plugins.html.api.accessibility.AccessibilityUtils.isHiddenFromScreenReader;

@Rule(key = "ImgWithoutAltCheck")
public class ImgWithoutAltCheck extends AbstractPageCheck {
  private static final String MESSAGE = "Provide alternative text for this element.";

  private final Deque<TagNode> objects = new ArrayDeque<>();
  private final Set<TagNode> objectsWithAlternativeText = new HashSet<>();

  @Override
  public void startDocument(List<Node> nodes) {
    objects.clear();
    objectsWithAlternativeText.clear();
  }

  @Override
  public void startElement(TagNode node) {
    if (!objects.isEmpty() && !isHiddenFromScreenReader(node)) {
      objectsWithAlternativeText.add(objects.peek());
    }

    if (requiresAlternativeText(node)) {
      createViolation(node, MESSAGE);
    }

    if (isObjectTag(node)) {
      objects.push(node);
      if (hasObjectAlternativeText(node)) {
        objectsWithAlternativeText.add(node);
      }
    }
  }

  @Override
  public void endElement(TagNode node) {
    if (isObjectTag(node) && !objects.isEmpty()) {
      TagNode object = objects.pop();
      if (!objectsWithAlternativeText.remove(object)) {
        createViolation(object, MESSAGE);
      }
    }
  }

  @Override
  public void endDocument() {
    objects.clear();
    objectsWithAlternativeText.clear();
  }

  @Override
  public void characters(TextNode textNode) {
    if (!objects.isEmpty() && textNode.getParent() == objects.peek() && !textNode.isBlank()) {
      objectsWithAlternativeText.add(objects.peek());
    }
  }

  @Override
  public void directive(DirectiveNode node) {
    if (!objects.isEmpty() && node.getParent() == objects.peek()) {
      objectsWithAlternativeText.add(objects.peek());
    }
  }

  @Override
  public void expression(ExpressionNode node) {
    if (!objects.isEmpty()) {
      objectsWithAlternativeText.add(objects.peek());
    }
  }

  /**
   * Returns whether the current element should raise S1077 immediately.
   *
   * @param node the element being visited
   * @return {@code true} when the element is missing its required alternative text
   */
  private static boolean requiresAlternativeText(TagNode node) {
    return (isImgTag(node) && !hasImgAlternativeText(node)) ||
      ((isImageInput(node) || isAreaTag(node)) && !hasRequiredAlternativeText(node));
  }

  private static boolean isImgTag(TagNode node) {
    return "IMG".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isObjectTag(TagNode node) {
    return "OBJECT".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isImageInput(TagNode node) {
    String type = node.getPropertyValue("TYPE");
    return "INPUT".equalsIgnoreCase(node.getNodeName()) && "IMAGE".equalsIgnoreCase(type);
  }

  private static boolean isAreaTag(TagNode node) {
    return "AREA".equalsIgnoreCase(node.getNodeName());
  }

  /**
   * Returns whether an image element exposes alternative text.
   *
   * @param node the image element to inspect
   * @return {@code true} when the image has an alt-like alternative text
   */
  private static boolean hasImgAlternativeText(TagNode node) {
    return node.hasProperty("alt") || hasAccessibleName(node) || hasThymeleafAltAttribute(node);
  }

  /**
   * Returns whether an area or image button exposes a non-empty alternative text.
   *
   * @param node the area or input element to inspect
   * @return {@code true} when a non-empty alt-like alternative text is present
   */
  private static boolean hasRequiredAlternativeText(TagNode node) {
    return hasNonEmptyAttribute(node, "alt") || hasAccessibleName(node);
  }

  /**
   * Returns whether an object tag exposes alternative text without inspecting its descendants.
   *
   * @param node the object element to inspect
   * @return {@code true} when the object already provides alternative text
   */
  private static boolean hasObjectAlternativeText(TagNode node) {
    return hasNonEmptyAttribute(node, "title") || hasAccessibleName(node) || hasGeneratedText(node);
  }

  /**
   * Returns whether the element has a non-empty accessible name.
   *
   * @param node the element to inspect
   * @return {@code true} when aria-label or aria-labelledby is set
   */
  private static boolean hasAccessibleName(TagNode node) {
    return hasNonEmptyAttribute(node, "aria-label") || hasNonEmptyAttribute(node, "aria-labelledby");
  }

  /**
   * Returns whether an attribute is present with a non-empty value, including Thymeleaf variants.
   *
   * @param node the element to inspect
   * @param attributeName the attribute name to resolve
   * @return {@code true} when the attribute exists with a non-empty value
   */
  private static boolean hasNonEmptyAttribute(TagNode node, String attributeName) {
    String value = node.getPropertyValue(attributeName);
    if (value != null) {
      return !value.trim().isEmpty();
    }

    String thymeleafValue = node.getAttribute("th:" + attributeName.toLowerCase(Locale.ROOT));
    return (thymeleafValue != null && !thymeleafValue.trim().isEmpty()) ||
      hasThymeleafAttributeAssignment(node, attributeName);
  }

  /**
   * Returns whether the element renders text through a template-specific attribute.
   *
   * @param node the element to inspect
   * @return {@code true} when a text-generating attribute is present
   */
  private static boolean hasGeneratedText(TagNode node) {
    return node.hasAttribute("th:text") || node.hasAttribute("th:utext") || node.hasAttribute("v-text") || node.hasAttribute("v-html");
  }

  /**
   * In Thymeleaf there are multiple ways of specifying the alt for an img tag:
   * - using the th:alt or th:alt-title attributes (th:alt-title would set the title and alt to the same value)
   * - using the th:attr attribute for specifying different attributes. Example "th:attr="src=@{logo.png},title=#{logo},alt=#{logo}""
   */
  private static boolean hasThymeleafAltAttribute(TagNode node) {
    return node.hasProperty("th:alt") || node.hasProperty("th:alt-title") || hasThymeleafAttributeAssignment(node, "alt");
  }

  /**
   * Returns whether a Thymeleaf th:attr assignment sets the given attribute.
   *
   * @param node the element to inspect
   * @param attributeName the attribute name to look for
   * @return {@code true} when th:attr assigns the given attribute
   */
  private static boolean hasThymeleafAttributeAssignment(TagNode node, String attributeName) {
    String thAttrValue = node.getAttribute("th:attr");
    return thAttrValue != null && thAttrValue.contains(attributeName + "=");
  }
}
