/*
 * SonarQube HTML
 * Copyright (C) 2010-2025 SonarSource SA
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

class AriaProptypesCheckTest {

  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  void html() {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/AriaProptypesCheck.html"),
      new AriaProptypesCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(9).withMessage("The value of the attribute \"aria-hidden\" must be a boolean.")
      .next().atLine(13).withMessage("The value of the attribute \"aria-label\" must be a string.")
      .next().atLine(14)
      .next().atLine(20).withMessage("The value of the attribute \"aria-checked\" must be a boolean or the string \"mixed\".")
      .next().atLine(26).withMessage("The value of the attribute \"aria-valuemax\" must be a number.")
      .next().atLine(32).withMessage("The value of the attribute \"aria-posinset\" must be a integer.")
      .next().atLine(38)
      .next().atLine(43)
      .next().atLine(44)
      .next().atLine(49).withMessage("The value of the attribute \"aria-controls\" must be a list of strings that represent DOM element IDs (idlist).")
      .next().atLine(53).withMessage("The value of the attribute \"aria-details\" must be a string that represents a DOM element ID.")
      .noMore();
  }

  @Test
  void cshtml() {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/AriaProptypesCheck.cshtml"),
      new AriaProptypesCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(5).withMessage("The value of the attribute \"aria-checked\" must be a boolean or the string \"mixed\".")
      .next().atLine(7).withMessage("The value of the attribute \"aria-checked\" must be a boolean or the string \"mixed\".")
      .next().atLine(13).withMessage("The value of the attribute \"aria-valuemax\" must be a number.")
      .next().atLine(15).withMessage("The value of the attribute \"aria-valuemax\" must be a number.")
      .noMore();
  }
}
