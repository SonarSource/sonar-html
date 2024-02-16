/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (c) 2010-2024 SonarSource SA and Matthijs Galesloot
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
package org.sonar.plugins.html.checks.accessibility;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

import java.io.File;

class HeadingHasAccessibleContentCheckTest {
  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  void html() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/HeadingHasAccessibleContentCheck.html"), new HeadingHasAccessibleContentCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(8).withMessage("Headings must have content and the content must be accessible by a screen reader.")
      .next().atLine(9)
      .next().atLine(10)
      .next().atLine(11)
      .next().atLine(12)
      .next().atLine(13)
      .next().atLine(15)
      .next().atLine(16)
      .next().atLine(17)
      .next().atLine(18)
      .next().atLine(19)
      .next().atLine(20)
      .next().atLine(23)
      .next().atLine(26)
      .next().atLine(33)
      .next().atLine(34)
      .next().atLine(37)
      .next().atLine(43)
      .next().atLine(72);
  }

  @Test
  void jsp() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/HeadingHasAccessibleContentCheck.jsp"), new HeadingHasAccessibleContentCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(2);
  }

  @Test
  void php() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/HeadingHasAccessibleContentCheck.php"), new HeadingHasAccessibleContentCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(2);
  }

  @Test
  void vue() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/HeadingHasAccessibleContentCheck.vue"), new HeadingHasAccessibleContentCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(2)
      .next().atLine(3)
      .next().atLine(9)
      .next().atLine(10);
  }
}
