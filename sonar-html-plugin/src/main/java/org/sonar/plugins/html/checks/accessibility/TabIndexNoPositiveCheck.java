/*
 * SonarQube HTML
 * Copyright (C) 2010-2025 SonarSource Sàrl
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

@Rule(key = "S6841")
public class TabIndexNoPositiveCheck extends AbstractPageCheck {

  @Override
  public void startElement(TagNode element) {
    var tabIndex = element.getAttribute("tabIndex");
    if (tabIndex == null) {
      return;
    }
    try {
      int tabIndexValue = Integer.parseInt(tabIndex);
      if (tabIndexValue > 0) {
        createViolation(element, "Avoid using positive values for the \"tabIndex\" attribute.");
      }
    } catch (NumberFormatException e) {
      // Report nothing
    }
  }
}
