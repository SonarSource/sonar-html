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
package org.sonar.plugins.html.checks.sonar;

import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;

import java.util.List;

@Rule(key = "S1436")
public class ElementWithGivenIdPresentCheck extends AbstractPageCheck {

  @RuleProperty(
    key = "id",
    description = "Value of the \"id\" attribute expected to be present on every page")
  public String id = "";

  private boolean foundId;

  @Override
  public void startDocument(List<Node> nodes) {
    foundId = false;
  }

  @Override
  public void startElement(TagNode node) {
    if (id.equals(node.getPropertyValue("id"))) {
      foundId = true;
    }
  }

  @Override
  public void endDocument() {
    if (!id.isEmpty() && !foundId) {
      createViolation(0, "The ID \"" + id + "\" is missing from this page and should be added.");
    }
  }

}
