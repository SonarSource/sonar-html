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

import java.io.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

class AnchorsHaveContentCheckTest {

  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  void html() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/AnchorsHaveContentCheck.html"),
      new AnchorsHaveContentCheck());

      checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(1).withMessage("Anchors must have content and the content must be accessible by a screen reader.")
        .next().atLine(2)
        .next().atLine(3)
        .next().atLine(4)
        .next().atLine(5)
        .noMore();
  }

  @Test
  void php() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/AnchorsHaveContentCheck.php"),
      new AnchorsHaveContentCheck());

      checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(1)
        .next().atLine(2)
        .noMore();
  }
}
