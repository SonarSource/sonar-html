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

import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.DirectiveNode;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;

import java.util.List;

@Rule(key = "DoctypePresenceCheck")
public class DoctypePresenceCheck extends AbstractPageCheck {

  private boolean foundDoctype;
  private boolean reported;

  @Override
  public void startDocument(List<Node> nodes) {
    foundDoctype = false;
    reported = false;
  }

  @Override
  public void directive(DirectiveNode node) {
    if (isDoctype(node)) {
      foundDoctype = true;
    }
  }

  private static boolean isDoctype(DirectiveNode node) {
    return "DOCTYPE".equalsIgnoreCase(node.getNodeName());
  }

  @Override
  public void startElement(TagNode node) {
    if (isHtml(node) && !foundDoctype && !reported) {
      createViolation(node, "Insert a <!DOCTYPE> declaration to before this <" + node.getNodeName() + "> tag.");
      reported = true;
    }
  }

  private static boolean isHtml(TagNode node) {
    return "HTML".equalsIgnoreCase(node.getNodeName());
  }

}
