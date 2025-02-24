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
package org.sonar.plugins.html.lex;

import java.util.List;

import org.sonar.plugins.html.node.ExpressionNode;
import org.sonar.plugins.html.node.Node;

/**
 * Tokenizer for expressions.
 *

 */
class ExpressionTokenizer extends AbstractTokenizer<List<Node>> {

  public ExpressionTokenizer(String startChars, String endChars) {
    super(startChars, endChars);
  }

  @Override
  Node createNode() {
    return new ExpressionNode();
  }

}
