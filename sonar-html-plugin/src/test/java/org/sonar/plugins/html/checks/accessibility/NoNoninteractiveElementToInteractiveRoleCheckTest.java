/*
 * SonarQube HTML
 * Copyright (C) 2010-2024 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
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
  void valid() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/NoNoninteractiveElementToInteractiveRoleCheck/valid.html"),
      new NoNoninteractiveElementToInteractiveRoleCheck());

  checkMessagesVerifier.verify(sourceCode.getIssues())
      .noMore();
  }

  @Test
  void invalid() throws Exception {
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
      .next().atLine(38)
      .next().atLine(39)
      .next().atLine(40)
      .next().atLine(41)
      .next().atLine(42)
      .next().atLine(43)
      .next().atLine(44)
      .next().atLine(45)
      .next().atLine(46)
      .next().atLine(47)
      .next().atLine(48)
      .next().atLine(49)
      .next().atLine(50)
      .next().atLine(51)
      .next().atLine(52)
      .next().atLine(53)
      .next().atLine(54)
      .next().atLine(55)
      .next().atLine(56)
      .next().atLine(57)
      .next().atLine(58)
      .next().atLine(59)
      .next().atLine(60)
      .next().atLine(61)
      .next().atLine(62)
      .next().atLine(63)
      .next().atLine(64)
      .next().atLine(65)
      .next().atLine(66)
      .next().atLine(67)
      .next().atLine(68)
      .next().atLine(69)
      .next().atLine(70)
      .next().atLine(71)
      .next().atLine(72)
      .next().atLine(73)
      .next().atLine(74)
      .next().atLine(75)
      .next().atLine(76)
      .next().atLine(78)
      .next().atLine(79)
      .next().atLine(80)
      .next().atLine(81)
      .noMore();

  }
}
