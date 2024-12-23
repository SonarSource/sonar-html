/*
 * SonarSource HTML analyzer :: Sonar Plugin
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

import java.util.List;

import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;
import org.sonar.plugins.html.node.TextNode;

@Rule(key = "S5264")
public class ObjectWithAlternativeContentCheck extends AbstractPageCheck {

  private TagNode object;

  @Override
  public void startDocument(List<Node> nodes) {
    object = null;
  }

  @Override
  public void startElement(TagNode node) {
    object = isObject(node) ? node : null;
  }

  @Override
  public void endElement(TagNode node) {
    if (isObject(node) && object != null) {
      createViolation(object, "Add an accessible content to this \"<object>\" tag.");
      object = null;
    }
  }

  @Override
  public void characters(TextNode textNode) {
    if (!textNode.isBlank()) {
      object = null;
    }
  }
  
  private static boolean isObject(TagNode node) {
    return "OBJECT".equalsIgnoreCase(node.getNodeName());
  }
}
