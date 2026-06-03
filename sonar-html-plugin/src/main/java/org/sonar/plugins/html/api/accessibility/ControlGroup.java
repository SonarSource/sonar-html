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
package org.sonar.plugins.html.api.accessibility;

import java.util.Locale;
import java.util.Set;
import javax.annotation.CheckForNull;
import org.sonar.plugins.html.node.TagNode;

public class ControlGroup {
  private static final String INPUT_TAG = "input";
  // All valid HTML input type values per WHATWG; anything not in this set has an
  // invalid-value default of the Text state.
  private static final Set<String> VALID_INPUT_TYPES = Set.of(
    "hidden", "text", "search", "tel", "url", "email", "password",
    "date", "month", "week", "time", "datetime-local", "number", "range", "color",
    "checkbox", "radio", "file", "submit", "image", "reset", "button"
  );

  public static boolean belongsToAutofillExpectationMantleControlGroup(TagNode node) {
    if (!node.getNodeName().equalsIgnoreCase(INPUT_TAG)) {
      return true;
    }

    var type = resolveType(node);

    return type == null || !type.equalsIgnoreCase("hidden");
  }

  public static boolean belongsToDateControlGroup(TagNode node) {
    if (belongsToTextControlGroup(node)) {
      return true;
    }

    var type = resolveType(node);

    return type != null && type.equalsIgnoreCase("date");
  }

  public static boolean belongsToMonthControlGroup(TagNode node) {
    var type = resolveType(node);

    if (type != null && type.equalsIgnoreCase("month")) {
      return true;
    }

    return belongsToTextControlGroup(node);
  }

  public static boolean belongsToMultilineControlGroup(TagNode node) {
    var nodeName = node.getNodeName();

    if (nodeName.equalsIgnoreCase("textarea") || nodeName.equalsIgnoreCase("select")) {
      return true;
    }

    if (!nodeName.equalsIgnoreCase(INPUT_TAG)) {
      return false;
    }

    var type = resolveType(node);

    return type != null && type.equalsIgnoreCase("hidden");
  }

  public static boolean belongsToNumericControlGroup(TagNode node) {
    var type = resolveType(node);

    if (type != null && type.equalsIgnoreCase("number")) {
      return true;
    }

    return belongsToTextControlGroup(node);
  }

  public static boolean belongsToPasswordControlGroup(TagNode node) {
    var type = resolveType(node);

    if (type != null && type.equalsIgnoreCase("password")) {
      return true;
    }

    return belongsToTextControlGroup(node);
  }

  public static boolean belongsToTelControlGroup(TagNode node) {
    var type = resolveType(node);

    if (type != null && type.equalsIgnoreCase("tel")) {
      return true;
    }

    return belongsToTextControlGroup(node);
  }

  public static boolean belongsToTextControlGroup(TagNode node) {
    var nodeName = node.getNodeName();

    if (nodeName.equalsIgnoreCase("textarea") || nodeName.equalsIgnoreCase("select")) {
      return true;
    }

    if (!nodeName.equalsIgnoreCase(INPUT_TAG)) {
      return false;
    }

    var type = resolveType(node);

    if (type == null) {
      // No type attribute, or a dynamic binding whose value cannot be statically
      // resolved: treat conservatively as Text to avoid false positives.
      return true;
    }

    // WHATWG invalid-value default is also Text state.
    if (!VALID_INPUT_TYPES.contains(type.toLowerCase(Locale.ROOT))) {
      return true;
    }

    return type.equalsIgnoreCase("hidden") || type.equalsIgnoreCase("text") || type.equalsIgnoreCase("search");
  }

  public static boolean belongsToUrlControlGroup(TagNode node) {
    var type = resolveType(node);

    if (type != null && type.equalsIgnoreCase("url")) {
      return true;
    }

    return belongsToTextControlGroup(node);
  }

  public static boolean belongsToUsernameControlGroup(TagNode node) {
    var type = resolveType(node);

    if (type != null && type.equalsIgnoreCase("email")) {
      return true;
    }

    return belongsToTextControlGroup(node);
  }

  // Returns true when the node has a bound type attribute (e.g. [type], :type) whose
  // value is a dynamic expression that cannot be statically resolved. String literals
  // like [type]="'checkbox'" are resolvable and therefore not dynamic.
  public static boolean hasDynamicTypeBinding(TagNode node) {
    var typeAttr = node.getProperty("type");
    if (typeAttr == null || typeAttr.getName().equalsIgnoreCase("type")) {
      return false;
    }
    return !isStaticStringLiteral(typeAttr.getValue());
  }

  // Returns the resolved type value via getProperty, which covers static attributes
  // and all Angular/Vue binding forms ([type], :type, v-bind:type).
  // For bound attributes, JS string literals (e.g. "'checkbox'") are unwrapped to
  // their inner value; dynamic expressions that cannot be statically resolved return null.
  @CheckForNull
  private static String resolveType(TagNode node) {
    var typeAttr = node.getProperty("type");
    if (typeAttr == null) {
      return null;
    }
    var value = typeAttr.getValue();
    // Plain static attribute
    if (typeAttr.getName().equalsIgnoreCase("type")) {
      return value;
    }
    // Bound attribute: unwrap JS string literal (e.g. "'checkbox'" → "checkbox")
    if (isStaticStringLiteral(value)) {
      String inner = value.substring(1, value.length() - 1).trim();
      return inner.isEmpty() ? null : inner;
    }
    // Dynamic expression → unknown
    return null;
  }

  private static boolean isStaticStringLiteral(@CheckForNull String value) {
    if (value == null || value.length() < 2) {
      return false;
    }
    char first = value.charAt(0);
    char last = value.charAt(value.length() - 1);
    return (first == '\'' && last == '\'') || (first == '"' && last == '"');
  }

  private ControlGroup() {
  }
}
