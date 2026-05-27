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
package org.sonar.plugins.html.api;

import java.util.ArrayList;
import java.util.List;
import org.sonar.plugins.html.node.TagNode;

/**
 * Helpers for inspecting Thymeleaf {@code th:attr} bundles on a {@link TagNode}.
 *
 * <p>Thymeleaf lets multiple attribute assignments share a single {@code th:attr}, for
 * example {@code th:attr="src=@{/logo.png}, title=#{logo}, alt=#{logo}"}. Splitting that
 * value on commas naively is wrong because commas can legitimately appear inside Thymeleaf
 * expressions ({@code #{f(a,b)}}, {@code @{/path(x,y)}}) and inside quoted literals
 * ({@code 'Hello, world'}). This class handles that splitting and the named-assignment
 * lookups on top of it.
 */
public final class Thymeleaf {

  private Thymeleaf() {
  }

  /**
   * Returns whether {@code th:attr} on the node assigns a value to the given attribute.
   * Presence check only — the assigned value may itself be empty.
   */
  public static boolean hasAttrAssignment(TagNode node, String attributeName) {
    return getAttrAssignmentValue(node, attributeName) != null;
  }

  /**
   * Returns the raw value assigned to {@code attributeName} via {@code th:attr}, or
   * {@code null} when no such assignment exists. The returned value may be a Thymeleaf
   * literal (e.g. {@code "'foo'"}, {@code "''"}) or expression (e.g. {@code "#{label}"}).
   */
  public static String getAttrAssignmentValue(TagNode node, String attributeName) {
    String thAttrValue = node.getAttribute("th:attr");
    if (thAttrValue == null) {
      return null;
    }

    for (String assignment : splitAssignments(thAttrValue)) {
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

  /**
   * Returns whether a Thymeleaf {@code th:attr} assignment value represents an empty value,
   * i.e. the empty string, or a quoted literal containing only whitespace
   * (e.g. {@code "''"}, {@code "' '"}, {@code "\"\""}).
   */
  public static boolean isEmptyAssignmentValue(String value) {
    if (value.isEmpty()) {
      return true;
    }
    if (isQuotedLiteral(value)) {
      return value.substring(1, value.length() - 1).trim().isEmpty();
    }
    return false;
  }

  private static List<String> splitAssignments(String thAttrValue) {
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
        case '\'', '\"':
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
