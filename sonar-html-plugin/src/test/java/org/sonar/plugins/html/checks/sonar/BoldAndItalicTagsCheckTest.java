/*
 * SonarQube HTML
 * Copyright (C) 2010-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource Sàrl.
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

class BoldAndItalicTagsCheckTest {

  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  void detected() {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/BoldAndItalicTagsCheck.html"), new BoldAndItalicTagsCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLocation(1, 0, 1, 3).withMessage("Replace this <b> tag by <strong>.")
        .next().atLine(5).withMessage("Replace this <i> tag by <em>.")
        .next().atLine(7).withMessage("Replace this <B> tag by <strong>.")
        .next().atLine(11).withMessage("Replace this <i> tag by <em>.")
        .next().atLine(17).withMessage("Replace this <i> tag by <em>.")
        .next().atLine(19).withMessage("Replace this <i> tag by <em>.")
        .next().atLine(21).withMessage("Replace this <i> tag by <em>.")
        .next().atLine(25).withMessage("Replace this <i> tag by <em>.");
  }

}
