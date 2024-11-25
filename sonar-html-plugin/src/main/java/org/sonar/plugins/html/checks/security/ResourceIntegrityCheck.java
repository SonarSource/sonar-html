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
package org.sonar.plugins.html.checks.security;

import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Attribute;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "S5725")
public class ResourceIntegrityCheck extends AbstractPageCheck {

  @Override
  public void startElement(TagNode node) {
    if (node.equalsElementName("script")) {
      Attribute src = node.getProperty("src");
      if (src != null && isExternal(src.getValue()) && !node.hasProperty("integrity")) {
        createViolation(node, "Make sure not using resource integrity feature is safe here.");
      }
    }
  }

  private static boolean isExternal(String srcValue) {
    return srcValue.startsWith("http") || srcValue.startsWith("//");
  }

}
