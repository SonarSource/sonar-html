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

class LabelHasAssociatedControlCheckTest {
  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  void nesting() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/LabelHasAssociatedControlCheck/nesting.html"),
      new LabelHasAssociatedControlCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(1).withMessage("A form label must be associated with a control.")
      .next().atLine(2)
      .next().atLine(3)
      .next().atLine(9)
      .next().atLine(11)
      .next().atLine(16)
      .noMore();
  }

  @Test
  void forAttribute() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/LabelHasAssociatedControlCheck/for.html"),
      new LabelHasAssociatedControlCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(2).withMessage("A form label must be associated with a control.")
      .next().atLine(3)
      .next().atLine(6)
      .next().atLine(8)
      .next().atLine(10)
      .next().atLine(12)
      .next().atLine(14)
      .noMore();
  }

  @Test
  void php() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/LabelHasAssociatedControlCheck/bundle.php"),
      new LabelHasAssociatedControlCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(1).withMessage("A form label must be associated with a control.")
      .noMore();
  }

  @Test
  void jsp() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(
            new File("src/test/resources/checks/LabelHasAssociatedControlCheck/bundle.jsp"),
            new LabelHasAssociatedControlCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
            .next().atLine(1).withMessage("A form label must be associated with a control.")
            .noMore();
  }

  @Test
  void phtml() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(
            new File("src/test/resources/checks/LabelHasAssociatedControlCheck/file.phtml"),
            new LabelHasAssociatedControlCheck());
    checkMessagesVerifier.verify(sourceCode.getIssues())
            .noMore();
  }
}
