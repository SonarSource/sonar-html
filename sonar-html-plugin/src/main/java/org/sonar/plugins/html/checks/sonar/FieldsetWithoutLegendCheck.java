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
package org.sonar.plugins.html.checks.sonar;

import java.util.List;
import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "FieldsetWithoutLegendCheck")
public class FieldsetWithoutLegendCheck extends AbstractPageCheck {

  private boolean foundLegend;
  private TagNode fieldset;

  @Override
  public void startDocument(List<Node> nodes) {
    fieldset = null;
  }

  @Override
  public void startElement(TagNode node) {
    if (isFieldSet(node)) {
      foundLegend = false;
      fieldset = node;
    } else if (isLegend(node)) {
      foundLegend = true;
    }
  }

  @Override
  public void endElement(TagNode node) {
    if (isFieldSet(node)) {
      if (!foundLegend && fieldset != null) {
        createViolation(fieldset, "Add a <legend> tag to this fieldset.");
      }

      foundLegend = false;
      fieldset = null;
    }
  }

  private static boolean isFieldSet(TagNode node) {
    return "FIELDSET".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isLegend(TagNode node) {
    return "LEGEND".equalsIgnoreCase(node.getNodeName());
  }

}
