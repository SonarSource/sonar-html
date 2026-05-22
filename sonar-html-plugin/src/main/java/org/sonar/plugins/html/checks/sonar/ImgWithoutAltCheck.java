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

import java.util.Locale;
import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "ImgWithoutAltCheck")
public class ImgWithoutAltCheck extends AbstractPageCheck {
  private static final String MESSAGE = "Provide alternative text for this element.";

  @Override
  public void startElement(TagNode node) {
    if (requiresAlternativeText(node)) {
      createViolation(node, MESSAGE);
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
