/*
 * SonarQube HTML
 * Copyright (C) 2010-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource Sàrl.
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

import java.util.regex.Pattern;

@Rule(key = "S6844")
public class AnchorsShouldNotBeUsedAsButtonsCheck extends AbstractPageCheck {
  static boolean isAJavascriptHandler(String value) {
    String pattern = "^\\W*?javascript:.*";

    return Pattern.matches(pattern, value);
  }

  @Override
  public void startElement(TagNode node) {
    if ("a".equalsIgnoreCase(node.getNodeName())) {
      String onClickAttribute = node.getAttribute("onclick");

      if (onClickAttribute == null) {
        return;
      }
      String hrefAttribute = node.getAttribute("href");

      if (hrefAttribute == null || hrefAttribute.isBlank() || "#".equals(hrefAttribute) || isAJavascriptHandler(hrefAttribute)) {
        createViolation(node, "Anchor tags should not be used as buttons.");
      }
    }
  }
}
