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
package org.sonar.plugins.html.checks.sonar;


import java.io.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

class PageWithoutTitleCheckTest {

  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  void detected() {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/PageWithoutTitleCheck.html"), new PageWithoutTitleCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(1).withMessage("Add a <title> tag to this page.")
        .next().atLine(5)
        .next().atLine(15)
        .next().atLine(21)
        .next().atLine(25)
        .next().atLine(31)
        .next().atLine(37);
  }

}
