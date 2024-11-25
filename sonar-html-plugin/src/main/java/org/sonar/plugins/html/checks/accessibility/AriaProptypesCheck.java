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

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import org.sonar.check.Rule;
import org.sonar.plugins.html.api.Helpers;
import org.sonar.plugins.html.api.accessibility.Aria;
import org.sonar.plugins.html.api.accessibility.AriaProperty;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.api.accessibility.Aria.AriaPropertyValues;
import org.sonar.plugins.html.api.accessibility.Aria.AriaPropertyType;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "S6793")
public class AriaProptypesCheck extends AbstractPageCheck {

  @Override
  public void startElement(TagNode element) {
    var attributes = element.getAttributes();
    for (var attribute : attributes) {
      var name = attribute.getName();
      var normalizedName = name.toLowerCase(Locale.ENGLISH);

      var property = Aria.getProperty(AriaProperty.of(normalizedName));
      if (property == null) {
        continue;
      }

      var value = attribute.getValue();
      if (Helpers.isDynamicValue(value)) {
        continue;
      }

      if (!isValid(property, value)) {
        createViolation(element.getStartLinePosition(), message(name, property.getType(), property.getValues()));
      }
    }
  }

  private static boolean isValid(AriaPropertyValues property, String value) {
    var expectedType = property.getType();
    var allowUndefined = property.getAllowUndefined().orElse(false);
    var expectedValues = property.getValues();

    if (allowUndefined.booleanValue() && ("".equalsIgnoreCase(value))) {
      return true;
    }

    switch (expectedType) {
      case BOOLEAN:
        return "true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value);
      case STRING, ID:
        return !value.isBlank();
      case TRISTATE:
        return "true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value) || "mixed".equalsIgnoreCase(value);
      case INTEGER, NUMBER:
        return value.matches("^-?\\d+$");
      case TOKEN:
        return expectedValues.contains(value.toLowerCase(Locale.ENGLISH));
      case IDLIST:
        var ids = Arrays.asList(value.split("\\s+"));
        return ids.stream().allMatch(id -> !id.isBlank());
      case TOKENLIST:
        var tokens = Arrays.asList(value.split("\\s+"));
        return tokens.stream().allMatch(token -> expectedValues.contains(token.toLowerCase(Locale.ENGLISH)));
      default:
        // Should never happen
        return false;
    }
  }

  private static String message(String name, AriaPropertyType type, Set<String> values) {
    switch (type) {
      case TRISTATE:
        return "The value of the attribute \"" + name + "\" must be a boolean or the string \"mixed\".";
      case TOKEN:
        return "The value of the attribute \"" + name + "\" must be a single token from the following: " + values.stream().collect(Collectors.joining(", ")) + ".";
      case TOKENLIST:
        return "The value of the attribute \"" + name + "\" must be a list of one or more tokens from the following: " + values.stream().collect(Collectors.joining(", ")) + ".";
      case IDLIST:
        return "The value of the attribute \"" + name + "\" must be a list of strings that represent DOM element IDs (idlist).";
      case ID:
        return "The value of the attribute \"" + name + "\" must be a string that represents a DOM element ID.";
      case BOOLEAN, STRING, INTEGER, NUMBER:
      default:
        return "The value of the attribute \"" + name + "\" must be a " + type + ".";
    }
  }
}
