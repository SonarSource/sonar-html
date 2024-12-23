/*
 * SonarQube HTML
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
import org.sonar.check.RuleProperty;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;
import org.sonar.plugins.html.node.TextNode;

@Rule(key = "LongJavaScriptCheck")
public class LongJavaScriptCheck extends AbstractPageCheck {

  private static final int DEFAULT_MAX_LINES = 5;

  private final StringBuilder text = new StringBuilder();

  private TagNode scriptNode;


  @RuleProperty(
    key = "maxLines",
    description = "Max Lines (Number)",
    defaultValue = "" + DEFAULT_MAX_LINES)
  public int maxLines = DEFAULT_MAX_LINES;

  @Override
  public void startElement(TagNode node) {
    if ("script".equalsIgnoreCase(node.getNodeName())) {
      scriptNode = node;
      text.delete(0, text.length());
    }
  }

  @Override
  public void characters(TextNode textNode) {
    if (scriptNode != null) {
      text.append(textNode.getCode());
    }
  }

  @Override
  public void endElement(TagNode element) {
    if ("script".equalsIgnoreCase(element.getNodeName()) && scriptNode != null) {
      int linesOfCode = (int) text.toString().trim().lines().count();
      if (linesOfCode > maxLines) {
        createViolation(scriptNode.getStartLinePosition(),
          "The length of this JS script (" + linesOfCode + ") exceeds the maximum set to " + maxLines + ".",
          (double) linesOfCode - (double) maxLines);
      }
      scriptNode = null;
    }
  }

}
