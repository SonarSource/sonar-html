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
package org.sonar.plugins.html.lex;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.sonar.plugins.html.node.Node;
import org.sonar.sslr.channel.CodeReader;

import static org.assertj.core.api.Assertions.assertThat;

public class TextTokenizerTest {

  @Test
  public void emptyCodeReader() {
    List<Node> nodeList = new ArrayList<>();
    CodeReader emptyReader = new CodeReader("");

    TextTokenizer tokenizer = new TextTokenizer();
    tokenizer.consume(emptyReader, nodeList);

    assertThat(nodeList).hasSize(1);
    assertThat(nodeList.get(0).getCode()).isEmpty();
  }

}
