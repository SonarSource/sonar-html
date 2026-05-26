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

import java.util.ArrayList;
import java.util.List;
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
    if (value != null && !value.trim().isEmpty()) {
      return true;
    }

    String thymeleafValue = node.getAttribute("th:" + attributeName.toLowerCase(Locale.ROOT));
    if (thymeleafValue != null) {
      return !thymeleafValue.trim().isEmpty();
    }

    String thymeleafAssignedValue = getThymeleafAttributeAssignmentValue(node, attributeName);
    return thymeleafAssignedValue != null && !isEmptyThymeleafValue(thymeleafAssignedValue);
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
    return getThymeleafAttributeAssignmentValue(node, attributeName) != null;
  }

  private static String getThymeleafAttributeAssignmentValue(TagNode node, String attributeName) {
    String thAttrValue = node.getAttribute("th:attr");
    if (thAttrValue == null) {
      return null;
    }

    for (String assignment : splitThymeleafAssignments(thAttrValue)) {
      int separatorIndex = assignment.indexOf('=');
      if (separatorIndex < 0) {
        continue;
      }

      String assignedAttribute = assignment.substring(0, separatorIndex).trim();
      if (attributeName.equalsIgnoreCase(assignedAttribute)) {
        return assignment.substring(separatorIndex + 1).trim();
      }
    }

    return null;
  }

  private static List<String> splitThymeleafAssignments(String thAttrValue) {
    List<String> assignments = new ArrayList<>();
    StringBuilder currentAssignment = new StringBuilder();
    char quotedBy = 0;
    int nestedBraces = 0;
    int nestedParentheses = 0;
    int nestedBrackets = 0;

    for (int index = 0; index < thAttrValue.length(); index++) {
      char currentCharacter = thAttrValue.charAt(index);
      if (quotedBy != 0) {
        currentAssignment.append(currentCharacter);
        if (currentCharacter == quotedBy && !isEscaped(thAttrValue, index)) {
          quotedBy = 0;
        }
        continue;
      }

      switch (currentCharacter) {
        case '\'':
        case '\"':
          quotedBy = currentCharacter;
          currentAssignment.append(currentCharacter);
          break;
        case '{':
          nestedBraces++;
          currentAssignment.append(currentCharacter);
          break;
        case '}':
          nestedBraces = Math.max(0, nestedBraces - 1);
          currentAssignment.append(currentCharacter);
          break;
        case '(':
          nestedParentheses++;
          currentAssignment.append(currentCharacter);
          break;
        case ')':
          nestedParentheses = Math.max(0, nestedParentheses - 1);
          currentAssignment.append(currentCharacter);
          break;
        case '[':
          nestedBrackets++;
          currentAssignment.append(currentCharacter);
          break;
        case ']':
          nestedBrackets = Math.max(0, nestedBrackets - 1);
          currentAssignment.append(currentCharacter);
          break;
        case ',':
          if (nestedBraces == 0 && nestedParentheses == 0 && nestedBrackets == 0) {
            addAssignment(assignments, currentAssignment);
            currentAssignment.setLength(0);
          } else {
            currentAssignment.append(currentCharacter);
          }
          break;
        default:
          currentAssignment.append(currentCharacter);
          break;
      }
    }

    addAssignment(assignments, currentAssignment);
    return assignments;
  }

  private static boolean isEmptyThymeleafValue(String value) {
    if (value.isEmpty()) {
      return true;
    }

    if (isQuotedLiteral(value)) {
      return value.substring(1, value.length() - 1).trim().isEmpty();
    }

    return false;
  }

  private static boolean isQuotedLiteral(String value) {
    return value.length() >= 2 &&
      ((value.charAt(0) == '\'' && value.charAt(value.length() - 1) == '\'') ||
        (value.charAt(0) == '\"' && value.charAt(value.length() - 1) == '\"'));
  }

  private static boolean isEscaped(String value, int quoteIndex) {
    int backslashCount = 0;
    for (int index = quoteIndex - 1; index >= 0 && value.charAt(index) == '\\'; index--) {
      backslashCount++;
    }
    return backslashCount % 2 != 0;
  }

  private static void addAssignment(List<String> assignments, StringBuilder currentAssignment) {
    String assignment = currentAssignment.toString().trim();
    if (!assignment.isEmpty()) {
      assignments.add(assignment);
    }
  }
}
