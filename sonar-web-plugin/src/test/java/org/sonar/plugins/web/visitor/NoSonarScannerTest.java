/*
 * SonarSource :: Web :: Sonar Plugin
 * Copyright (c) 2010-2017 SonarSource SA and Matthijs Galesloot
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
package org.sonar.plugins.web.visitor;

import com.google.common.base.Charsets;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.issue.NoSonarFilter;
import org.sonar.plugins.web.lex.PageLexer;
import org.sonar.plugins.web.node.Node;

import java.io.StringReader;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
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
    WebSourceCode webSourceCode = new WebSourceCode(new DefaultInputFile("key", "dummy.jsp"));

    NoSonarFilter noSonarFilter = spy(new NoSonarFilter());
    HtmlAstScanner pageScanner = new HtmlAstScanner(Collections.emptyList());
    pageScanner.addVisitor(new NoSonarScanner(noSonarFilter));

    pageScanner.scan(nodeList, webSourceCode, Charsets.UTF_8);

    verify(noSonarFilter, times(1)).noSonarInFile(any(InputFile.class), isOnlyIgnoringLine2());
  }

  private Set isOnlyIgnoringLine2() {
    return argThat(new IsOnlyIgnoringLine2());
  }

  private class IsOnlyIgnoringLine2 extends ArgumentMatcher<Set> {

    public boolean matches(Object set) {
      Set<Integer> lines = (Set) set;
      return lines.size() == 1 && lines.contains(2);
    }
  }
}
