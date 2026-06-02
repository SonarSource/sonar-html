/*
 * SonarQube HTML
 * Copyright (C) SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * You can redistribute and/or modify this program under the terms of
 * the Sonar Source-Available License Version 1, as published by SonarSource Sàrl.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package org.sonar.plugins.html.checks.security;

import java.io.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

class WebviewSandboxingCheckTest {

  private static final String MESSAGE = "Change this code to enable sandboxing.";

  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  void test() {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/webviewSandboxingCheck.html"), new WebviewSandboxingCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLocation(1, 9, 1, 24).withMessage(MESSAGE)
      .next().atLocation(2, 9, 2, 24).withMessage(MESSAGE)
      .next().atLocation(3, 9, 3, 24).withMessage(MESSAGE)
      .next().atLocation(4, 9, 4, 24).withMessage(MESSAGE)
      .next().atLocation(5, 9, 5, 23).withMessage(MESSAGE)
      .next().atLocation(6, 9, 6, 23).withMessage(MESSAGE)
      .next().atLocation(8, 9, 8, 24).withMessage(MESSAGE)
      .next().atLocation(8, 32, 8, 46).withMessage(MESSAGE)
      .next().atLocation(9, 9, 9, 23).withMessage(MESSAGE)
      .next().atLocation(10, 9, 10, 23).withMessage(MESSAGE)
      .next().atLocation(11, 9, 11, 23).withMessage(MESSAGE)
      .next().atLocation(12, 9, 12, 23).withMessage(MESSAGE)
      .next().atLocation(13, 9, 13, 23).withMessage(MESSAGE)
      .next().atLocation(17, 9, 17, 24).withMessage(MESSAGE)
      .next().atLocation(21, 2, 21, 16).withMessage(MESSAGE)
      .next().atLocation(23, 9, 23, 24).withMessage(MESSAGE)
      .next().atLocation(24, 9, 24, 23).withMessage(MESSAGE)
      .next().atLocation(25, 9, 25, 23).withMessage(MESSAGE)
      .next().atLocation(26, 9, 26, 23).withMessage(MESSAGE)
      .next().atLocation(27, 9, 27, 23).withMessage(MESSAGE);
  }
}
