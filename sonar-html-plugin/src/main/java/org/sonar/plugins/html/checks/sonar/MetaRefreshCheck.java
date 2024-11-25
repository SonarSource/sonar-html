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
package org.sonar.plugins.html.checks.sonar;

import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "MetaRefreshCheck")
public class MetaRefreshCheck extends AbstractPageCheck {

  @Override
  public void startElement(TagNode node) {
    if (isMetaRefreshTag(node)) {
      createViolation(node, "Remove this meta refresh tag.");
    }
  }

  private static boolean isMetaRefreshTag(TagNode node) {
    String httpEquiv = node.getAttribute("HTTP-EQUIV");

    return "META".equalsIgnoreCase(node.getNodeName()) &&
      httpEquiv != null &&
      "REFRESH".equalsIgnoreCase(httpEquiv);
  }

}
