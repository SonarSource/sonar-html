/*
 * SonarSource HTML analyzer :: Sonar Plugin
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
package org.sonar.plugins.html.checks.accessibility;

import java.io.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

class AnchorsHaveContentCheckTest {

  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  void html() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/AnchorsHaveContentCheck.html"),
      new AnchorsHaveContentCheck());

      checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(1).withMessage("Anchors must have content and the content must be accessible by a screen reader.")
        .next().atLine(2)
        .next().atLine(3)
        .next().atLine(4)
        .next().atLine(5)
        .noMore();
  }

  @Test
  void php() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/AnchorsHaveContentCheck.php"),
      new AnchorsHaveContentCheck());

      checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(1)
        .next().atLine(2)
        .noMore();
  }
}
