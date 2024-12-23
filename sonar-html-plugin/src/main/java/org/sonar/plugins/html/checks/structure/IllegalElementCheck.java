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
package org.sonar.plugins.html.checks.structure;

import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;

import java.util.List;

@Rule(key = "IllegalElementCheck")
public class IllegalElementCheck extends AbstractPageCheck {

  private static final String DEFAULT_ELEMENTS = "";

  @RuleProperty(
    key = "elements",
    description = "Comma-separated list of names of forbidden elements",
    defaultValue = DEFAULT_ELEMENTS)
  public String elements = DEFAULT_ELEMENTS;

  private String[] elementsArray;

  @Override
  public void startDocument(List<Node> nodes) {
    elementsArray = trimSplitCommaSeparatedList(elements);
  }

  @Override
  public void startElement(TagNode element) {
    for (String elementName : elementsArray) {
      if (elementName.equalsIgnoreCase(element.getLocalName()) || elementName.equalsIgnoreCase(element.getNodeName())) {
        createViolation(element, "Remove this \"" + elementName + "\" element.");
      }
    }
  }

}
