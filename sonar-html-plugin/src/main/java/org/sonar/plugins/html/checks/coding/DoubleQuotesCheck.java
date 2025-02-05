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
package org.sonar.plugins.html.checks.coding;

import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Attribute;
import org.sonar.plugins.html.node.TagNode;


@Rule(key = "DoubleQuotesCheck")
public class DoubleQuotesCheck extends AbstractPageCheck {

  @Override
  public void startElement(TagNode element) {
    for (Attribute a : element.getAttributes()) {
      if (a.getValue() != null && a.getValue().trim().length() > 0 && isSingleQuoteAttribute(a)) {
        createViolation(element.getStartLinePosition(), "Use double quotes instead of single ones.");
        // no more than one violation per element
        break;
      }
    }
  }

  /**
   * Single quoted attributes are allowed only when used because the value contains a double quote.
   */
  private static boolean isSingleQuoteAttribute(Attribute a) {
    return a.isSingleQuoted() && !a.getValue().contains("\"");
  }

}
