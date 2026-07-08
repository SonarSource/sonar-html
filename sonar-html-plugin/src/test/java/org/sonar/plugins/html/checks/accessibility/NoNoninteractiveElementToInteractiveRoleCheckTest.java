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

class NoNoninteractiveElementToInteractiveRoleCheckTest {
  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  void valid() {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/NoNoninteractiveElementToInteractiveRoleCheck/valid.html"),
      new NoNoninteractiveElementToInteractiveRoleCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .noMore();
  }

  @Test
  void invalid() {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/NoNoninteractiveElementToInteractiveRoleCheck/invalid.html"),
      new NoNoninteractiveElementToInteractiveRoleCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(1).withMessage("Non-interactive elements should not be assigned interactive roles.")
      .next().atLine(2)
      .next().atLine(3)
      .next().atLine(4)
      .next().atLine(5)
      .next().atLine(6)
      .next().atLine(7)
      .next().atLine(8)
      .next().atLine(9)
      .next().atLine(10)
      .next().atLine(11)
      .next().atLine(12)
      .next().atLine(13)
      .next().atLine(14)
      .next().atLine(15)
      .next().atLine(16)
      .next().atLine(17)
      .next().atLine(18)
      .next().atLine(19)
      .next().atLine(20)
      .next().atLine(21)
      .next().atLine(22)
      .next().atLine(23)
      .next().atLine(24)
      .next().atLine(25)
      .next().atLine(26)
      .next().atLine(27)
      .next().atLine(28)
      .next().atLine(29)
      .next().atLine(30)
      .next().atLine(31)
      .next().atLine(32)
      .next().atLine(33)
      .next().atLine(34)
      .next().atLine(35)
      .next().atLine(36)
      .next().atLine(37)
      .noMore();
  }

  @Test
  void contextLi() {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/NoNoninteractiveElementToInteractiveRoleCheck/contextLi.html"),
      new NoNoninteractiveElementToInteractiveRoleCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(2)
      .next().atLine(5)
      .next().atLine(11)
      .next().atLine(17)
      .noMore();
  }

  @Test
  void contextImg() {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/NoNoninteractiveElementToInteractiveRoleCheck/contextImg.html"),
      new NoNoninteractiveElementToInteractiveRoleCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(4)
      .next().atLine(5)
      .next().atLine(6)
      .noMore();
  }

  @Test
  void contextFigure() {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/NoNoninteractiveElementToInteractiveRoleCheck/contextFigure.html"),
      new NoNoninteractiveElementToInteractiveRoleCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(4)
      .next().atLine(7)
      .noMore();
  }

  @Test
  void contextLabel() {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/NoNoninteractiveElementToInteractiveRoleCheck/contextLabel.html"),
      new NoNoninteractiveElementToInteractiveRoleCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(2)
      .next().atLine(3)
      .noMore();
  }
}
