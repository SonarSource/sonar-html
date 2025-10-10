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

class AbsoluteURICheckTest {

  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  void detected() {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/absoluteURICheck.html"), new AbsoluteURICheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLocation(3, 0, 3, 30).withMessage("Replace this absolute URI \"href\" with a relative one, or move this absolute URI to a configuration file.")
      .next().atLine(4).withMessage("Replace this absolute URI \"href\" with a relative one, or move this absolute URI to a configuration file.")
      .next().atLine(7).withMessage("Replace this absolute URI \"src\" with a relative one, or move this absolute URI to a configuration file.")
      .next().atLine(8).withMessage("Replace this absolute URI \"src\" with a relative one, or move this absolute URI to a configuration file.")
      .next().atLine(9).withMessage("Replace this absolute URI \"src\" with a relative one, or move this absolute URI to a configuration file.");
  }

  @Test
  void custom() {
    AbsoluteURICheck check = new AbsoluteURICheck();
    check.attributes = "img.src";

    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/absoluteURICheck.html"), check);

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(7).withMessage("Replace this absolute URI \"src\" with a relative one, or move this absolute URI to a configuration file.")
      .next().atLine(8).withMessage("Replace this absolute URI \"src\" with a relative one, or move this absolute URI to a configuration file.")
      .next().atLine(9).withMessage("Replace this absolute URI \"src\" with a relative one, or move this absolute URI to a configuration file.");
  }

}
