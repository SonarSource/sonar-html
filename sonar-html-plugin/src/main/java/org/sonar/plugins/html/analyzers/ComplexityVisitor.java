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
package org.sonar.plugins.html.analyzers;

import java.util.List;
import java.util.Set;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Attribute;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;

public class ComplexityVisitor extends AbstractPageCheck {

  private static final Set<String> OPERATORS = Set.of("&&", "||", "and", "or");
  private static final Set<String> TAGS = Set.of("catch", "choose", "if", "forEach", "forTokens", "when");

  private int complexity;

  @Override
  public void startDocument(List<Node> nodes) {
    complexity = 1;
  }

  @Override
  public void endDocument() {
    getHtmlSourceCode().addMeasure(CoreMetrics.COMPLEXITY, complexity);
  }

  @Override
  public void startElement(TagNode node) {
    // count jstl tags
    if (TAGS.contains(node.getNodeName()) || TAGS.contains(node.getLocalName())) {
      complexity++;
      return;
    }

    // count complexity in expressions
    for (Attribute a : node.getAttributes()) {
      if (isUnifiedExpression(a.getValue())) {
        String[] tokens = a.getValue().split("[ \t\n]");

        for (String token : tokens) {
          if (OPERATORS.contains(token)) {
            complexity++;
          }
        }
      }
    }
  }

}
