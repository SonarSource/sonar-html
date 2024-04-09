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

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import org.sonar.check.Rule;
import org.sonar.plugins.html.api.accessibility.Aria;
import org.sonar.plugins.html.api.accessibility.Property;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.api.accessibility.Aria.AriaProperty;
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

      var property = Aria.getProperty(Property.of(normalizedName));
      if (property == null) {
        continue;
      }

      var value = attribute.getValue();
      if (isDynamicValue(value)) {
        continue;
      }

      if (!isValid(property, value)) {
        createViolation(element.getStartLinePosition(), message(name, property.getType(), property.getValues()));
      }
    }
  }

  private static boolean isDynamicValue(String value) {
    return value.startsWith("<?php") || value.startsWith("{{") || value.startsWith("{%");
  }

  private static boolean isValid(AriaProperty property, String value) {
    var expectedType = property.getType();
    var allowUndefined = property.getAllowUndefined().orElse(false);
    var expectedValues = property.getValues();

    if (allowUndefined.booleanValue() && ("".equalsIgnoreCase(value))) {
      return true;
    }

    switch (expectedType) {
      case BOOLEAN:
        return "true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value);
      case STRING:
      case ID:
        return !value.isBlank();
      case TRISTATE:
        return "true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value) || "mixed".equalsIgnoreCase(value);
      case INTEGER:
      case NUMBER:
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
      case BOOLEAN:
      case STRING:
      case INTEGER:
      case NUMBER:
      default:
        return "The value of the attribute \"" + name + "\" must be a " + type + ".";
    }
  }
}
