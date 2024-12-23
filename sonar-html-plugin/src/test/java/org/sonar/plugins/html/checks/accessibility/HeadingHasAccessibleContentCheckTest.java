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
package org.sonar.plugins.html.checks.accessibility;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

import java.io.File;

class HeadingHasAccessibleContentCheckTest {
  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  void html() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/HeadingHasAccessibleContentCheck/file.html"), new HeadingHasAccessibleContentCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(8).withMessage("Headings must have content and the content must be accessible by a screen reader.")
      .next().atLine(9)
      .next().atLine(10)
      .next().atLine(11)
      .next().atLine(12)
      .next().atLine(13)
      .next().atLine(15)
      .next().atLine(16)
      .next().atLine(17)
      .next().atLine(18)
      .next().atLine(19)
      .next().atLine(20)
      .next().atLine(23)
      .next().atLine(26)
      .next().atLine(33)
      .next().atLine(34)
      .next().atLine(37)
      .next().atLine(43)
      .next().atLine(72);
  }

  @Test
  void jsp() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/HeadingHasAccessibleContentCheck/file.jsp"), new HeadingHasAccessibleContentCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(2);
  }

  @Test
  void php() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/HeadingHasAccessibleContentCheck/file.php"), new HeadingHasAccessibleContentCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(2);
  }

  @Test
  void vue() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/HeadingHasAccessibleContentCheck/file.vue"), new HeadingHasAccessibleContentCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(2)
      .next().atLine(3)
      .next().atLine(9)
      .next().atLine(10);
  }
}
