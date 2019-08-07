/*
 * SonarHTML :: SonarQube Plugin
 * Copyright (c) 2010-2019 SonarSource SA and Matthijs Galesloot
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
package org.sonar.plugins.html.visitor;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.sonar.api.issue.NoSonarFilter;
import org.sonar.plugins.html.lex.PageLexer;
import org.sonar.plugins.html.node.Node;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Matthijs Galesloot
 */
public class NoSonarScannerTest {

  @Test
  public void scanNoSonar() {
    List<Node> nodeList = new PageLexer().parse(new StringReader("<table>\n<!-- //NOSONAR --><td>\n</table>"));
    HtmlSourceCode htmlSourceCode = new HtmlSourceCode(new TestInputFileBuilder("key", "dummy.jsp").build());

    NoSonarFilter noSonarFilter = spy(new NoSonarFilter());
    HtmlAstScanner pageScanner = new HtmlAstScanner(Collections.emptyList());
    pageScanner.addVisitor(new NoSonarScanner(noSonarFilter));

    pageScanner.scan(nodeList, htmlSourceCode, StandardCharsets.UTF_8);

    verify(noSonarFilter, times(1)).noSonarInFile(any(InputFile.class), isOnlyIgnoringLine2());
  }

  private Set<Integer> isOnlyIgnoringLine2() {
    return argThat(new IsOnlyIgnoringLine2());
  }

  private static class IsOnlyIgnoringLine2 implements ArgumentMatcher<Set<Integer>> {

    @Override
    public boolean matches(Set<Integer> lines) {
      return lines.size() == 1 && lines.contains(2);
    }
  }
}
