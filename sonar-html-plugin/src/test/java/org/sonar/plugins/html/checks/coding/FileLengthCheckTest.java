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
package org.sonar.plugins.html.checks.coding;

import java.io.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

import static org.assertj.core.api.Assertions.assertThat;

class FileLengthCheckTest {

  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  void detected() {
    assertThat(new FileLengthCheck().maxLength).isEqualTo(1000);
  }

  @Test
  void custom() {
    FileLengthCheck check = new FileLengthCheck();
    check.maxLength = 2;

    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/FileLengthCheck.html"), check);

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(null).withMessage("Current file has 3 lines, which is greater than 2 authorized. Split it into smaller files.");
  }

  @Test
  void custom_ok() {
    FileLengthCheck check = new FileLengthCheck();
    check.maxLength = 3;

    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/FileLengthCheck.html"), check);

    checkMessagesVerifier.verify(sourceCode.getIssues());
  }

}
