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
package org.sonar.plugins.html.checks.attributes;

import java.io.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

class NoAccessKeyCheckTest {

  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  void detected() {
    NoAccessKeyCheck check = new NoAccessKeyCheck();

    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/NoAccessKeyCheck.html"), check);

    checkMessagesVerifier.verify(sourceCode.getIssues())
            .next().atLocation(1, 1, 1, 5).withMessage("No access key attribute allowed. Inconsistencies between keyboard shortcuts and keyboard commands used by screenreaders and keyboard-only users create a11y complications.")
            .next().atLocation(3, 1, 3, 5)
            .next().atLocation(7, 1, 7, 11);
  }
}
