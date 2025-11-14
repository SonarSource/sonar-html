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
package org.sonar.plugins.html.checks.security;

import java.io.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

class DisabledAutoescapeCheckTest {
  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  void test_jinja() {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/jinjaAutoescape.html"), new DisabledAutoescapeCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLocation(6, 35, 6, 57).withMessage("Make sure disabling auto-escaping feature is safe here.")
      .next().atLocation(7, 35, 7, 55).withMessage("Make sure disabling auto-escaping feature is safe here.")
      .next().atLocation(8, 35, 8, 55).withMessage("Make sure disabling auto-escaping feature is safe here.")
      .next().atLocation(9, 34, 11, 40).withMessage("Make sure disabling auto-escaping feature is safe here.")
      .next().atLocation(12, 42, 12, 49).withMessage("Make sure disabling auto-escaping feature is safe here.")
      .next().atLocation(13, 43, 13, 51).withMessage("Make sure disabling auto-escaping feature is safe here.")
      .next().atLocation(16, 12, 16, 32).withMessage("Make sure disabling auto-escaping feature is safe here.")
      .next().atLocation(19, 12, 19, 32).withMessage("Make sure disabling auto-escaping feature is safe here.")
      .next().atLocation(19, 60, 19, 80).withMessage("Make sure disabling auto-escaping feature is safe here.");
  }
}
