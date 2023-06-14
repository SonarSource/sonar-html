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

public class DoctypePresenceCheckTest {

  @Rule
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  public void doctype_before_html() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/DoctypePresenceCheck/DoctypeBeforeHtml.html"), new DoctypePresenceCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues());
  }

  @Test
  public void full_doctype_before_html() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/DoctypePresenceCheck/FullDoctypeBeforeHtml.html"), new DoctypePresenceCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues());
  }

  @Test
  public void no_doctype_before_foo() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/DoctypePresenceCheck/NoDoctypeBeforeFoo.html"), new DoctypePresenceCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues());
  }

  @Test
  public void no_doctype_before_html() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/DoctypePresenceCheck/NoDoctypeBeforeHtml.html"), new DoctypePresenceCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLocation(3, 0, 3, 6).withMessage("Insert a <!DOCTYPE> declaration to before this <hTmL> tag.");
  }

  @Test
  public void multiple_html_tags() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/DoctypePresenceCheck/MultipleHtmlTags.html"), new DoctypePresenceCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(1);
  }

  @Test
  public void doctype_after_html() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/DoctypePresenceCheck/DoctypeAfterHtml.html"), new DoctypePresenceCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(1);
  }

}
