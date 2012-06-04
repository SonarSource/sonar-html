/*
 * Sonar Web Plugin
 * Copyright (C) 2010 Matthijs Galesloot
 * dev@sonar.codehaus.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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
package org.sonar.plugins.web.checks.comments;

import org.junit.Test;
import org.sonar.plugins.web.checks.AbstractCheckTester;
import org.sonar.plugins.web.visitor.WebSourceCode;

import java.io.StringReader;

import static org.fest.assertions.Assertions.assertThat;

public class AvoidCommentedOutCodeCheckTest extends AbstractCheckTester {

  @Test
  public void shouldDetectNoCommentedOutCode() {
    String fragment = "<!-- this comment is not allowed -->";

    WebSourceCode sourceCode = parseAndCheck(new StringReader(fragment), AvoidCommentedOutCodeCheck.class);

    assertThat(sourceCode.getViolations().size()).isEqualTo(0);
  }

  @Test
  public void shouldDetectNoCommentedOutCodeEvenWithGreaterThanAtTheEnd() {
    String fragment = "<!--  Hello world! >-->";

    WebSourceCode sourceCode = parseAndCheck(new StringReader(fragment), AvoidCommentedOutCodeCheck.class);

    assertThat(sourceCode.getViolations().size()).isEqualTo(0);
  }

  @Test
  public void shouldDetectCommentedOutCode() {
    String fragment = "<!--\n  <h:someOtherTag line='45' />\n  <h:someOtherTag line='45' />\n-->";

    WebSourceCode sourceCode = parseAndCheck(new StringReader(fragment), AvoidCommentedOutCodeCheck.class);

    assertThat(sourceCode.getViolations().size()).isEqualTo(1);
  }

  @Test
  public void shouldDetectCommentedOutCodeOnAttributes() {
    String fragment = "<!--\n style=\"size: 45px; color: red;\"\n id=\"foo\" name=\"bar\"\n id=\"foo\" name=\"bar\"\n -->";

    WebSourceCode sourceCode = parseAndCheck(new StringReader(fragment), AvoidCommentedOutCodeCheck.class);

    assertThat(sourceCode.getViolations().size()).isEqualTo(1);
  }

}
