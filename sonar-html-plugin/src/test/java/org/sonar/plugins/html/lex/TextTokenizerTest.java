/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (c) 2010-2023 SonarSource SA and Matthijs Galesloot
 * sonarqube@googlegroups.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
