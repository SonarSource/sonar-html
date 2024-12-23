/*
 * SonarQube HTML Plugin :: Sonar Plugin
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
package org.sonar.plugins.html.visitor;

import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.sonar.api.batch.sensor.issue.internal.DefaultNoSonarFilter;
import org.sonar.api.issue.NoSonarFilter;
import org.sonar.plugins.html.lex.PageLexer;
import org.sonar.plugins.html.node.Node;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class NoSonarScannerTest {
  private static final String CONTENT = "<table>\n<!-- //NOSONAR --><td>\n</table>";

  @Test
  public void scanNoSonar() throws Exception {
    List<Node> nodeList;
    try (Reader reader = new StringReader(CONTENT)) {
      nodeList = new PageLexer().parse(reader);
    }
    InputFile inputFile = new TestInputFileBuilder("key", "dummy.jsp")
      .setContents(CONTENT)
      .setCharset(StandardCharsets.UTF_8)
      .build();
    NoSonarFilter noSonarFilter = spy(new DefaultNoSonarFilter());
    HtmlAstScanner pageScanner = new HtmlAstScanner(Collections.emptyList());
    pageScanner.addVisitor(new NoSonarScanner(noSonarFilter));

    pageScanner.scan(nodeList, new HtmlSourceCode(inputFile));

    verify(noSonarFilter, times(1)).noSonarInFile(eq(inputFile), argThat(new IsOnlyIgnoringLine2()));
  }

  private static class IsOnlyIgnoringLine2 implements ArgumentMatcher<Set<Integer>> {

    @Override
    public boolean matches(Set<Integer> lines) {
      return lines.size() == 1 && lines.contains(2);
    }
  }
}
