/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (c) 2010-2022 SonarSource SA and Matthijs Galesloot
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

public class ElementWithGivenIdPresentCheckTest {

  @Rule
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  public void test() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/ElementWithGivenIdPresentCheck/Ok.html"), new ElementWithGivenIdPresentCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues());
  }

  @Test
  public void test_angular() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/ElementWithGivenIdPresentCheck/Angular_Ok.html"), new ElementWithGivenIdPresentCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues());
  }

  @Test
  public void custom_ko() throws Exception {
    ElementWithGivenIdPresentCheck check = new ElementWithGivenIdPresentCheck();
    check.id = "gotit";

    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/ElementWithGivenIdPresentCheck/Ko.html"), check);

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().withMessage("The ID \"gotit\" is missing from this page and should be added.");
  }

  @Test
  public void custom_ok() throws Exception {
    ElementWithGivenIdPresentCheck check = new ElementWithGivenIdPresentCheck();
    check.id = "gotit";

    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/ElementWithGivenIdPresentCheck/Ok.html"), check);

    checkMessagesVerifier.verify(sourceCode.getIssues());
  }

}
