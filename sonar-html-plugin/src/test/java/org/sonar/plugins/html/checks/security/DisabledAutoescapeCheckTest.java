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
package org.sonar.plugins.html.checks.security;

import java.io.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

class DisabledAutoescapeCheckTest {
  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  void test_jinja() {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/jinjaAutoescape.html"), new DisabledAutoescapeCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLocation(6, 35, 6, 57).withMessage("Make sure disabling auto-escaping feature is safe here.")
      .next().atLocation(7, 35, 7, 55).withMessage("Make sure disabling auto-escaping feature is safe here.")
      .next().atLocation(8, 35, 8, 55).withMessage("Make sure disabling auto-escaping feature is safe here.")
      .next().atLocation(9, 34, 11, 40).withMessage("Make sure disabling auto-escaping feature is safe here.")
      .next().atLocation(12, 42, 12, 49).withMessage("Make sure disabling auto-escaping feature is safe here.")
      .next().atLocation(13, 43, 13, 51).withMessage("Make sure disabling auto-escaping feature is safe here.")
      .next().atLocation(16, 12, 16, 32).withMessage("Make sure disabling auto-escaping feature is safe here.");
  }
}
