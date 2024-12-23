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
package org.sonar.plugins.html.checks.sonar;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

import java.io.File;

public class TableHeaderHasIdOrScopeCheckTest {

  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  public void detected() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/TableHeaderHasIdOrScopeCheck.html"), new TableHeaderHasIdOrScopeCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLocation(41, 8, 41, 12).withMessage("Add either an 'id' or a 'scope' attribute to this <th> tag.")
      .next().atLine(42).withMessage("Add either an 'id' or a 'scope' attribute to this <th> tag.")
      .next().atLine(70)
      .next().atLine(76)
      .next().atLine(101)
      .next().atLine(102)
      .next().atLine(103)
      .next().atLine(106)
      .next().atLine(111)
      .next().atLine(140)
      .next().atLine(141)
      .next().atLine(144)
      .next().atLine(149)
      .next().atLine(177)
      .next().atLine(178)
      .next().atLine(186)
      .next().atLine(209)
      .next().atLine(210)
      .next().atLine(214)
      .next().atLine(238)
      .next().atLine(239)
      .next().atLine(275)
      .next().atLine(285)
      .next().atLine(320)
      .next().atLine(327)
      .next().atLine(334)
      .next().atLine(380)
      .next().atLine(387)
      .next().atLine(387)
      .next().atLine(392)
      .next().atLine(393)
      .next().atLine(408)
      .next().atLine(415)
      .next().atLine(415)
      .next().atLine(420)
      .next().atLine(421)
      .next().atLine(494)
      .next().atLine(495)
      .next().atLine(506)
      .next().atLine(507)
      .next().atLine(519)
      .next().atLine(520)
      .next().atLine(570)
      .next().atLine(574)
      .next().atLine(605)
      .next().atLine(617)
      .next().atLine(621)
      .next().atLine(623)
    ;
  }

}
