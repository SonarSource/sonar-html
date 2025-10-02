/*
 * SonarQube HTML
 * Copyright (C) 2010-2025 SonarSource SA
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

import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Rule(key="S7926")
public class MetaAllowZoomCheck extends AbstractPageCheck {
  @Override
  public void startElement(TagNode element) {
    // Only check <meta> tags
    if (!"meta".equalsIgnoreCase(element.getNodeName())) {
      return;
    }

    String nameAttr = element.getAttribute("name");
    if (!"viewport".equalsIgnoreCase(nameAttr)) {
      return;
    }

    String content = element.getAttribute("content");
    if (content == null) {
      return;
    }

    var contentProps = parseContentAttribute(content);

    if (contentProps.containsKey("user-scalable")) {
      String value = contentProps.get("user-scalable");
      if ("no".equals(value) || "0".equals(value)) {
        createViolation(element, "Meta viewport disables zoom via user-scalable=no.");
        return;
      }
    }

    // Check for maximum-scale
    if (contentProps.containsKey("maximum-scale")) {
      String value = contentProps.get("maximum-scale");
      try {
        double scale = Double.parseDouble(value);
        if (scale < 2.0) {
          createViolation(element, "Meta viewport limits zoom with maximum-scale < 2.");
          return;
        }
      } catch (NumberFormatException e) {
        // Ignore invalid numbers
      }
    }
  }

  public static Map<String, String> parseContentAttribute(String content) {
    if (content.isBlank()) {
      return Map.of();
    }

    return Arrays.stream(content.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .map(s -> s.split("=", 2))
            .collect(Collectors.toMap(
                    arr -> arr[0].trim().toLowerCase(),
                    arr -> arr.length > 1 ? arr[1].trim() : ""
            ));
  }
}
