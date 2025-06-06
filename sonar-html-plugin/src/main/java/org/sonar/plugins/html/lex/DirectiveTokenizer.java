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

import org.sonar.plugins.html.node.DirectiveNode;
import org.sonar.plugins.html.node.Node;

/**
 * Tokenizer for directives.
 *

 */
class DirectiveTokenizer extends ElementTokenizer {

  public DirectiveTokenizer(String startToken, String endToken) {
    super(startToken, endToken);
  }

  @Override
  Node createNode() {

    return new DirectiveNode();
  }
}
