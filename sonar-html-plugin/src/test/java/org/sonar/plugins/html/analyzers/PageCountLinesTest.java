/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (c) 2010-2020 SonarSource SA and Matthijs Galesloot
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
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.sonar.api.internal.google.common.io.Files;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.plugins.html.api.HtmlConstants;
import org.sonar.plugins.html.lex.PageLexer;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.visitor.HtmlAstScanner;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

import static org.assertj.core.api.Assertions.assertThat;

public class PageCountLinesTest {

  private PageLexer lexer;
  private HtmlAstScanner scanner;

  @Before
  public void setUp() {
    lexer = new PageLexer();
    scanner = new HtmlAstScanner(Collections.emptyList());
    scanner.addVisitor(new PageCountLines());
  }

  @Test
  public void testCountLines() {
    List<Node> nodeList = lexer.parse(readFile("src/main/webapp/user-properties.jsp"));
    assertThat(nodeList.size()).isGreaterThan(100);

    HtmlSourceCode htmlSourceCode = createHtmlSourceCode("test/user-properties.jsp");
    scanner.scan(nodeList, htmlSourceCode);

    assertThat(htmlSourceCode.getMeasure(CoreMetrics.NCLOC)).isEqualTo(224);
    assertThat(htmlSourceCode.getDetailedLinesOfCode().size()).isEqualTo(224);
    assertThat(htmlSourceCode.getMeasure(CoreMetrics.COMMENT_LINES)).isEqualTo(14);
  }

  @Test
  public void testCountLinesHtmlFile() {
    List<Node> nodeList = lexer.parse(readFile("checks/AvoidHtmlCommentCheck/document.html"));

    HtmlSourceCode htmlSourceCode = createHtmlSourceCode("test/document.html");
    scanner.scan(nodeList, htmlSourceCode);

    assertThat(htmlSourceCode.getMeasure(CoreMetrics.NCLOC)).isEqualTo(8);
    assertThat(htmlSourceCode.getDetailedLinesOfCode()).containsOnly(1, 2, 3, 4, 6, 7, 8, 9);
    assertThat(htmlSourceCode.getMeasure(CoreMetrics.COMMENT_LINES)).isEqualTo(1);
  }

  @Test
  public void testCountLinesJspFile() {
    List<Node> nodeList = lexer.parse(readFile("checks/AvoidHtmlCommentCheck/document.jsp"));

    HtmlSourceCode htmlSourceCode = new HtmlSourceCode(new TestInputFileBuilder("key", "testdocument.jsp").setModuleBaseDir(new File(".").toPath()).build());
    scanner.scan(nodeList, htmlSourceCode);

    assertThat(htmlSourceCode.getMeasure(CoreMetrics.NCLOC)).isEqualTo(2);
    assertThat(htmlSourceCode.getDetailedLinesOfCode()).containsOnly(1, 3);
    assertThat(htmlSourceCode.getMeasure(CoreMetrics.COMMENT_LINES)).isEqualTo(6);
  }

  private HtmlSourceCode createHtmlSourceCode(String relativePath) {
    return new HtmlSourceCode(new TestInputFileBuilder("key", relativePath).setLanguage(HtmlConstants.LANGUAGE_KEY).setModuleBaseDir(new File(".").toPath()).build());
  }

  private Reader readFile(String fileName) {
    File root = new File("src/test/resources");
    File file = new File(root, fileName);
    try {
      return new StringReader(Files.toString(file, StandardCharsets.UTF_8));
    } catch (IOException e) {
      throw new IllegalArgumentException("Cannot read " + fileName, e);
    }
  }


}
