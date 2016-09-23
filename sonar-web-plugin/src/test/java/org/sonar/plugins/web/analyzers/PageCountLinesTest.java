/*
 * SonarSource :: Web :: Sonar Plugin
 * Copyright (c) 2010-2016 SonarSource SA and Matthijs Galesloot
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
package org.sonar.plugins.web.analyzers;

import com.google.common.base.Charsets;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.plugins.web.lex.PageLexer;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.visitor.HtmlAstScanner;
import org.sonar.plugins.web.visitor.WebSourceCode;
import org.sonar.test.TestUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collections;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

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
  public void testCountLines() throws FileNotFoundException {
    java.io.File file = TestUtils.getResource("src/main/webapp/user-properties.jsp");
    List<Node> nodeList = lexer.parse(new FileReader(file));
    assertThat(nodeList.size()).isGreaterThan(100);

    //  new File("test", "user-properties.jsp");
    String relativePath = "test/user-properties.jsp";
    WebSourceCode webSourceCode = new WebSourceCode(new DefaultInputFile("key", relativePath).setModuleBaseDir(new File(".").toPath()));
    scanner.scan(nodeList, webSourceCode, Charsets.UTF_8);

    assertThat(webSourceCode.getMeasure(CoreMetrics.NCLOC)).isEqualTo(227);
    assertThat(webSourceCode.getDetailedLinesOfCode().size()).isEqualTo(224);
    assertThat(webSourceCode.getMeasure(CoreMetrics.COMMENT_LINES)).isEqualTo(14);
    assertThat(webSourceCode.getDetailedLinesOfComments().size()).isEqualTo(14);
  }

  @Test
  public void testCountLinesHtmlFile() throws FileNotFoundException {
    List<Node> nodeList = lexer.parse(new FileReader(TestUtils.getResource("checks/AvoidHtmlCommentCheck/document.html")));

    String relativePath = "test/document.html";
    WebSourceCode webSourceCode = new WebSourceCode(new DefaultInputFile("key", relativePath).setModuleBaseDir(new File(".").toPath()));
    scanner.scan(nodeList, webSourceCode, Charsets.UTF_8);

    assertThat(webSourceCode.getMeasure(CoreMetrics.NCLOC)).isEqualTo(8);
    assertThat(webSourceCode.getDetailedLinesOfCode()).containsOnly(1, 2, 3, 4, 6, 7, 8, 9);
    assertThat(webSourceCode.getMeasure(CoreMetrics.COMMENT_LINES)).isEqualTo(1);
    assertThat(webSourceCode.getDetailedLinesOfComments()).containsOnly(5);
  }

  @Test
  public void testCountLinesJspFile() throws FileNotFoundException {
    List<Node> nodeList = lexer.parse(new FileReader(TestUtils.getResource("checks/AvoidHtmlCommentCheck/document.jsp")));

    String relativePath = "testdocument.jsp";
    WebSourceCode webSourceCode = new WebSourceCode(new DefaultInputFile("key", relativePath).setModuleBaseDir(new File(".").toPath()));
    scanner.scan(nodeList, webSourceCode, Charsets.UTF_8);

    assertThat(webSourceCode.getMeasure(CoreMetrics.NCLOC)).isEqualTo(2);
    assertThat(webSourceCode.getDetailedLinesOfCode()).containsOnly(1, 3);
    assertThat(webSourceCode.getMeasure(CoreMetrics.COMMENT_LINES)).isEqualTo(6);
    assertThat(webSourceCode.getDetailedLinesOfComments()).containsOnly(2, 4, 6, 7, 8, 10);
  }

}
