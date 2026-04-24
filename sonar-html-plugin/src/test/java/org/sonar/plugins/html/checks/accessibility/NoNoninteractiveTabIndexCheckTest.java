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
package org.sonar.plugins.html.checks.accessibility;

import java.io.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

class NoNoninteractiveTabIndexCheckTest {

  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  void test() {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/NoNoninteractiveTabIndexCheck.html"),
      new NoNoninteractiveTabIndexCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(18).withMessage("\"tabIndex\" should only be declared on interactive elements.")
      .next().atLine(19)
      .next().atLine(20)
      .next().atLine(23)
      .next().atLine(24)
      .next().atLine(25)
      .noMore();
  }
}
