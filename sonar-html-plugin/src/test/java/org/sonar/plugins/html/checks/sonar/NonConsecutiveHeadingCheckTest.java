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
package org.sonar.plugins.html.checks.sonar;


import java.io.File;

import org.junit.Rule;
import org.junit.Test;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

public class NonConsecutiveHeadingCheckTest {

  @Rule
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  public void no_heading_tags() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/NonConsecutiveHeadingCheck/NoHeadingTags.html"), new NonConsecutiveHeadingCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues());
  }

  @Test
  public void only_h1_tags() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/NonConsecutiveHeadingCheck/OnlyH1Tags.html"), new NonConsecutiveHeadingCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues());
  }

  @Test
  public void only_h2_tags() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/NonConsecutiveHeadingCheck/OnlyH2Tags.html"), new NonConsecutiveHeadingCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(1).withMessage("Do not skip level H1.");
  }

  @Test
  public void h2_with_h1() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/NonConsecutiveHeadingCheck/H2WithH1.html"), new NonConsecutiveHeadingCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues());
  }

  @Test
  public void h5_with_h4() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/NonConsecutiveHeadingCheck/H5WithH4.html"), new NonConsecutiveHeadingCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(1).withMessage("Do not skip level H3.");
  }

}
