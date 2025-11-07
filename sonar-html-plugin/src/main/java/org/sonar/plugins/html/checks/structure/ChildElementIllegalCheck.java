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
package org.sonar.plugins.html.checks.structure;

import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;

/**
 * Checker for illegal child elements.
 *
 * e.g. head cannnot have child element body.
 */
@Rule(key = "ChildElementIllegalCheck")
public class ChildElementIllegalCheck extends AbstractPageCheck {

  private static final String DEFAULT_CHILD = "";
  private static final String DEFAULT_PARENT = "";

  @RuleProperty(
    key = "child",
    description = "Name of the child element",
    defaultValue = DEFAULT_CHILD)
  public String child = DEFAULT_CHILD;

  @RuleProperty(
    key = "parent",
    description = "Name of the parent element",
    defaultValue = DEFAULT_PARENT)
  public String parent = DEFAULT_PARENT;

  @Override
  public void startElement(TagNode element) {
    if (element.equalsElementName(parent)) {
      for (TagNode childNode : element.getChildren()) {
        if (childNode.equalsElementName(child)) {
          createViolation(childNode, "Remove this \"" + child + "\" tag; it is not a legal child of \"" + parent + "\".");
        }
      }
    }
  }

}
