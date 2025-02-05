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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

import java.io.File;

class ElementWithRoleShouldHaveRequiredPropertiesCheckTest {
  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  void validHTML() {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/ElementWithRoleShouldHaveRequiredPropertiesCheckTest/valid.html"),
      new ElementWithRoleShouldHaveRequiredPropertiesCheck()
    );

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .noMore();
  }

  @Test
  void invalidHTML() {
    HtmlSourceCode invalidSourceCode = TestHelper.scan(
      new File("src/test/resources/checks/ElementWithRoleShouldHaveRequiredPropertiesCheckTest/invalid.html"),
      new ElementWithRoleShouldHaveRequiredPropertiesCheck()
    );

    checkMessagesVerifier.verify(invalidSourceCode.getIssues())
      .next().atLine(2).withMessage("The attribute \"aria-checked\" is required by the role \"checkbox\".")
      .next().atLine(3).withMessage("The attribute \"aria-controls\" is required by the role \"combobox\".")
      .next().atLine(3).withMessage("The attribute \"aria-expanded\" is required by the role \"combobox\".")
      .next().atLine(4).withMessage("The attribute \"aria-level\" is required by the role \"heading\".")
      .next().atLine(5).withMessage("The attribute \"aria-checked\" is required by the role \"menuitemcheckbox\".")
      .next().atLine(6).withMessage("The attribute \"aria-checked\" is required by the role \"menuitemradio\".")
      .next().atLine(7).withMessage("The attribute \"aria-valuenow\" is required by the role \"meter\".")
      .next().atLine(8).withMessage("The attribute \"aria-selected\" is required by the role \"option\".")
      .next().atLine(9).withMessage("The attribute \"aria-checked\" is required by the role \"radio\".")
      .next().atLine(10).withMessage("The attribute \"aria-controls\" is required by the role \"scrollbar\".")
      .next().atLine(10).withMessage("The attribute \"aria-valuenow\" is required by the role \"scrollbar\".")
      .next().atLine(11).withMessage("The attribute \"aria-valuenow\" is required by the role \"slider\".")
      .next().atLine(12).withMessage("The attribute \"aria-checked\" is required by the role \"switch\".")
      .next().atLine(13).withMessage("The attribute \"aria-selected\" is required by the role \"treeitem\".")
      .next().atLine(14).withMessage("The attribute \"aria-checked\" is required by the role \"radio\".")
      .next().atLine(15).withMessage("The attribute \"aria-selected\" is required by the role \"option\".")
    ;
  }
}
