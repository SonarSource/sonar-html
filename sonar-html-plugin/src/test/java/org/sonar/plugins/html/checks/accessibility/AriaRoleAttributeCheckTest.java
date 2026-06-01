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

class AriaRoleAttributeCheckTest {

  private static final String MESSAGE =
    "Replace \"aria-role\" with \"role\"; \"role\" is the correct WAI-ARIA attribute name.";

  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  void noncompliant() {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/AriaRoleAttributeCheck/noncompliant.html"),
      new AriaRoleAttributeCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(1).withMessage(MESSAGE)
      .next().atLine(2).withMessage(MESSAGE)
      .next().atLine(3).withMessage(MESSAGE)
      .next().atLine(4).withMessage(MESSAGE)
      .next().atLine(5).withMessage(MESSAGE)
      .next().atLine(6).withMessage(MESSAGE)
      .next().atLine(7).withMessage(MESSAGE)
      .next().atLine(8).withMessage(MESSAGE)
      .noMore();
  }

  @Test
  void compliant() {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/AriaRoleAttributeCheck/compliant.html"),
      new AriaRoleAttributeCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .noMore();
  }

  @Test
  void frameworks() {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/AriaRoleAttributeCheck/frameworks.html"),
      new AriaRoleAttributeCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(1).withMessage(MESSAGE)
      .next().atLine(2).withMessage(MESSAGE)
      .next().atLine(3).withMessage(MESSAGE)
      .next().atLine(4).withMessage(MESSAGE)
      .noMore();
  }
}
