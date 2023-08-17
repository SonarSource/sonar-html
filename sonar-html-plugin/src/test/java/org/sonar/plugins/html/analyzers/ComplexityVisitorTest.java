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
package org.sonar.plugins.html.analyzers;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.plugins.html.api.HtmlConstants;
import org.sonar.plugins.html.lex.PageLexer;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.visitor.HtmlAstScanner;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

import static org.assertj.core.api.Assertions.assertThat;

public class ComplexityVisitorTest {

  @Test
  public void testComplexity() throws IOException {
    PageLexer lexer = new PageLexer();
    HtmlAstScanner scanner = new HtmlAstScanner(List.of(new ComplexityVisitor()));
    List<Node> nodeList = lexer.parse(new StringReader(Files.readString(Path.of("src/test/resources/analyzers/complexity.jsp"))));
    assertThat(nodeList).isNotEmpty();

    HtmlSourceCode htmlSourceCode = new HtmlSourceCode(new TestInputFileBuilder("key", "analyzers/complexity.jsp").setLanguage(HtmlConstants.LANGUAGE_KEY).setModuleBaseDir(new File(".").toPath()).build());
    scanner.scan(nodeList, htmlSourceCode);
    assertThat(htmlSourceCode.getMeasure(CoreMetrics.COMPLEXITY)).isEqualTo(5);
  }

}
