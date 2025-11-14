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

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.plugins.html.api.HtmlConstants;
import org.sonar.plugins.html.lex.PageLexer;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.visitor.HtmlAstScanner;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

import static org.assertj.core.api.Assertions.assertThat;

class PageCountLinesTest {

  private PageLexer lexer;
  private HtmlAstScanner scanner;

  @BeforeEach
  void setUp() {
    lexer = new PageLexer();
    scanner = new HtmlAstScanner(Collections.emptyList());
    scanner.addVisitor(new PageCountLines());
  }

  @Test
  void testCountLines() {
    List<Node> nodeList = lexer.parse(readFile("src/main/webapp/user-properties.jsp"));
    assertThat(nodeList).hasSizeGreaterThan(100);

    HtmlSourceCode htmlSourceCode = createHtmlSourceCode("test/user-properties.jsp");
    scanner.scan(nodeList, htmlSourceCode);

    assertThat(htmlSourceCode.getMeasure(CoreMetrics.NCLOC)).isEqualTo(224);
    assertThat(htmlSourceCode.getDetailedLinesOfCode()).hasSize(224);
    assertThat(htmlSourceCode.getMeasure(CoreMetrics.COMMENT_LINES)).isEqualTo(14);
  }

  @Test
  void testCountLinesHtmlFile() {
    List<Node> nodeList = lexer.parse(readFile("checks/AvoidHtmlCommentCheck/document.html"));

    HtmlSourceCode htmlSourceCode = createHtmlSourceCode("test/document.html");
    scanner.scan(nodeList, htmlSourceCode);

    assertThat(htmlSourceCode.getMeasure(CoreMetrics.NCLOC)).isEqualTo(8);
    assertThat(htmlSourceCode.getDetailedLinesOfCode()).containsOnly(1, 2, 3, 4, 6, 7, 8, 9);
    assertThat(htmlSourceCode.getMeasure(CoreMetrics.COMMENT_LINES)).isEqualTo(1);
  }

  @Test
  void testCountLinesJspFile() {
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
    Path filePath = Paths.get("src", "test", "resources", fileName);
    try {
      return new StringReader(Files.readString(filePath, StandardCharsets.UTF_8));
    } catch (IOException e) {
      throw new IllegalArgumentException("Cannot read " + fileName, e);
    }
  }

}
