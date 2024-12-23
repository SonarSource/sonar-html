/*
 * SonarQube HTML Plugin :: Sonar Plugin
 * Copyright (C) 2010-2024 SonarSource SA
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
package org.sonar.plugins.html.checks.scripting;

import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "S4645")
public class NestedJavaScriptCheck extends AbstractPageCheck {

  private boolean insideScriptElement;

  @Override
  public void startElement(TagNode element) {
    if (element.equalsElementName("script")) {
      insideScriptElement = true;
    }
  }

  @Override
  public void endElement(TagNode element) {
    if (element.equalsElementName("script")) {
      if (!insideScriptElement) {
        createViolation(element.getEndLinePosition(), "A </script> was found without a relating opening <script> tag. This may be caused by nested script tags.");
      }
      insideScriptElement = false;
    }
  }
}
