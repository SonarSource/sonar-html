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

class WebviewDisableWebSecurityCheckTest {

  private static final String MESSAGE = "Change this code to enable web security.";

  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  /**
   * Verifies that disablewebsecurity is reported precisely on Electron webviews.
   */
  @Test
  void test() {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/webviewDisableWebSecurityCheck.html"), new WebviewDisableWebSecurityCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLocation(1, 9, 1, 27).withMessage(MESSAGE)
      .next().atLocation(2, 9, 2, 27).withMessage(MESSAGE)
      .next().atLocation(5, 2, 5, 20).withMessage(MESSAGE)
      .next().atLocation(9, 9, 9, 29).withMessage(MESSAGE);
  }
}
