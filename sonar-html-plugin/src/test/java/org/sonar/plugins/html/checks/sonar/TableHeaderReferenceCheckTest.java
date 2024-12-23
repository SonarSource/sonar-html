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

public class TableHeaderReferenceCheckTest {
  
  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  public void detected() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/TableHeaderReferenceCheck.html"), new TableHeaderReferenceCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLocation(35, 4, 35, 22).withMessage("id \"bar\" in \"headers\" does not reference any <th> header.")
        .next().atLine(56).withMessage("id \"bar\" in \"headers\" does not reference any <th> header.")
        .next().atLine(78).withMessage("id \"oof\" in \"headers\" does not reference any <th> header.")
        .next().atLine(89).withMessage("id \"bar\" in \"headers\" reference the header of another column/row.")
        .next().atLine(90).withMessage("id \"foo\" in \"headers\" reference the header of another column/row.")
        .next().atLine(108).withMessage("id \"oof\" in \"headers\" does not reference any <th> header.")
        .next().atLine(119).withMessage("id \"bar\" in \"headers\" reference the header of another column/row.")
        .next().atLine(123).withMessage("id \"foo\" in \"headers\" reference the header of another column/row.")
        .next().atLine(152).withMessage("id \"bar\" in \"headers\" does not reference any <th> header.")
        .next().atLine(220).withMessage("id \"bar\" in \"headers\" does not reference any <th> header.")
        .next().atLine(227).withMessage("id \"bar\" in \"headers\" does not reference any <th> header.")
        .next().atLine(275).withMessage("id \"foo\" in \"headers\" does not reference any <th> header.")
        .next().atLine(281).withMessage("id \"foo\" in \"headers\" does not reference any <th> header.");
  }
}
