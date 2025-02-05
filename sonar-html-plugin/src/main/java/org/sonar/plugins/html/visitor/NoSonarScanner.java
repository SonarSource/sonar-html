/*
 * SonarQube HTML
 * Copyright (C) 2010-2025 SonarSource SA
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
package org.sonar.plugins.html.visitor;

import org.sonar.api.issue.NoSonarFilter;
import org.sonar.plugins.html.node.CommentNode;
import org.sonar.plugins.html.node.ExpressionNode;
import org.sonar.plugins.html.node.Node;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Scans for //NOSONAR indicator.
 *

 */
public class NoSonarScanner extends DefaultNodeVisitor {

  private static final String NOSONAR = "//NOSONAR";
  private Set<Integer> noSonarLines;
  private final NoSonarFilter noSonarFilter;

  public NoSonarScanner(NoSonarFilter noSonarFilter) {
    this.noSonarFilter = noSonarFilter;
  }

  @Override
  public void startDocument(List<Node> nodes) {
    noSonarLines = new HashSet<>();
  }

  @Override
  public void comment(CommentNode node) {
    if (node.getCode().contains(NOSONAR)) {
      noSonarLines.add(node.getStartLinePosition());
    }
  }

  @Override
  public void expression(ExpressionNode node) {
    if (node.getCode().contains(NOSONAR)) {
      noSonarLines.add(node.getStartLinePosition());
    }
  }

  @Override
  public void endDocument() {
    if (noSonarLines != null && !noSonarLines.isEmpty()) {
      noSonarFilter.noSonarInFile(getHtmlSourceCode().inputFile(), noSonarLines);
    }
  }

}
