/*
 * SonarQube HTML
 * Copyright (C) 2010-2025 SonarSource Sàrl
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
  void nesting() {
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
  void forAttribute() {
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
  void php() {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/LabelHasAssociatedControlCheck/bundle.php"),
      new LabelHasAssociatedControlCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(1).withMessage("A form label must be associated with a control.")
      .noMore();
  }

  @Test
  void jsp() {
    HtmlSourceCode sourceCode = TestHelper.scan(
            new File("src/test/resources/checks/LabelHasAssociatedControlCheck/bundle.jsp"),
            new LabelHasAssociatedControlCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
            .next().atLine(1).withMessage("A form label must be associated with a control.")
            .noMore();
  }

  @Test
  void phtml() {
    HtmlSourceCode sourceCode = TestHelper.scan(
            new File("src/test/resources/checks/LabelHasAssociatedControlCheck/file.phtml"),
            new LabelHasAssociatedControlCheck());
    checkMessagesVerifier.verify(sourceCode.getIssues())
            .noMore();
  }

  @Test
  void razor() {
    HtmlSourceCode sourceCode = TestHelper.scan(
            new File("src/test/resources/checks/LabelHasAssociatedControlCheck/razor.cshtml"),
            new LabelHasAssociatedControlCheck());
    checkMessagesVerifier.verify(sourceCode.getIssues())
            .next().atLine(15).withMessage("A form label must be associated with a control.")
            .next().atLine(16)
            .next().atLine(17)
            .noMore();
  }

  @Test
  void angularAndVueBindingSyntax() {
    HtmlSourceCode sourceCode = TestHelper.scan(
            new File("src/test/resources/checks/LabelHasAssociatedControlCheck/binding.html"),
            new LabelHasAssociatedControlCheck());
    checkMessagesVerifier.verify(sourceCode.getIssues())
            // Lines 2-3: Angular [for] binding with text/aria-label - compliant
            // Lines 6-8: Vue :for and v-bind:for binding with text - compliant
            // Lines 11-12: Angular [for] binding but no accessible label text - noncompliant
            .next().atLine(11).withMessage("A form label must be associated with a control.")
            .next().atLine(12)
            // Lines 15-16: No for attribute, no nested control - noncompliant
            .next().atLine(15)
            .next().atLine(16)
            .noMore();
  }
}
