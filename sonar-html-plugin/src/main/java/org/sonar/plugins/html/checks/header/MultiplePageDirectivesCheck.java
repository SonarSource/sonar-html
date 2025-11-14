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
package org.sonar.plugins.html.checks.header;

import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.DirectiveNode;
import org.sonar.plugins.html.node.Node;

import java.util.List;


@Rule(key = "MultiplePageDirectivesCheck")
public class MultiplePageDirectivesCheck extends AbstractPageCheck {

  private DirectiveNode node;
  private int pageDirectives;

  @Override
  public void directive(DirectiveNode node) {
    if (!node.isHtml() && "page".equalsIgnoreCase(node.getNodeName()) && !isImportDirective(node)) {
      pageDirectives++;
      this.node = node;
    }
  }

  @Override
  public void endDocument() {
    if (pageDirectives > 1) {
      createViolation(node.getStartLinePosition(), "Combine these " + pageDirectives + " page directives into one.");
    }
  }

  @Override
  public void startDocument(List<Node> nodes) {
    pageDirectives = 0;
  }

  private static boolean isImportDirective(DirectiveNode node) {
    return node.getAttributes().size() == 1 && node.getAttribute("import") != null;
  }

}
