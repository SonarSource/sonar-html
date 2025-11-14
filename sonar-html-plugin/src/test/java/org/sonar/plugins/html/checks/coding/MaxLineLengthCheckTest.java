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
package org.sonar.plugins.html.checks.coding;

import java.io.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

class MaxLineLengthCheckTest {

  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  void test() {
    HtmlSourceCode file = TestHelper.scan(new File("src/test/resources/checks/MaxLineLengthCheck.html"), new MaxLineLengthCheck());
    checkMessagesVerifier.verify(file.getIssues())
        .next().atLine(2).withMessage("Split this 121 characters long line (which is greater than 120 authorized).")
        .next().atLine(3).withMessage("Split this 122 characters long line (which is greater than 120 authorized).")
        .next().atLine(8).withMessage("Split this 133 characters long line (which is greater than 120 authorized).")
        .next().atLine(15).withMessage("Split this 137 characters long line (which is greater than 120 authorized).");
  }

  @Test
  void custom() {
    MaxLineLengthCheck check = new MaxLineLengthCheck();
    check.maxLength = 40;

    HtmlSourceCode file = TestHelper.scan(new File("src/test/resources/checks/MaxLineLengthCheck.html"), check);
    checkMessagesVerifier.verify(file.getIssues())
        .next().atLine(1)
        .next().atLine(2)
        .next().atLine(3)
        .next().atLine(6).withMessage("Split this 41 characters long line (which is greater than 40 authorized).")
        .next().atLine(8).withMessage("Split this 133 characters long line (which is greater than 40 authorized).")
        .next().atLine(15).withMessage("Split this 137 characters long line (which is greater than 40 authorized).");
  }

}
