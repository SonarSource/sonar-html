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
