/*
 * SonarQube HTML
 * Copyright (C) SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * You can redistribute and/or modify this program under the terms of
 * the Sonar Source-Available License Version 1, as published by SonarSource Sàrl.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package org.sonar.plugins.html.checks.attributes;

import java.io.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

class FormMethodAttributeCheckTest {

  private static final String MESSAGE = "Use an explicit valid \"method\" attribute on this \"<form>\" tag (\"get\", \"post\", or \"dialog\").";

  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  void invalid() {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/FormMethodAttributeCheck/invalid.html"),
      new FormMethodAttributeCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(2).withMessage(MESSAGE)
      .next().atLine(5).withMessage(MESSAGE)
      .next().atLine(8).withMessage(MESSAGE)
      .next().atLine(11).withMessage(MESSAGE)
      .next().atLine(14).withMessage(MESSAGE)
      .noMore();
  }

  @Test
  void valid() {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/FormMethodAttributeCheck/valid.html"),
      new FormMethodAttributeCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .noMore();
  }

  @Test
  void dynamicValues() {
    HtmlSourceCode cshtmlSourceCode = TestHelper.scan(
      new File("src/test/resources/checks/FormMethodAttributeCheck/dynamic.cshtml"),
      new FormMethodAttributeCheck());

    checkMessagesVerifier.verify(cshtmlSourceCode.getIssues())
      .noMore();

    HtmlSourceCode jspSourceCode = TestHelper.scan(
      new File("src/test/resources/checks/FormMethodAttributeCheck/dynamic.jsp"),
      new FormMethodAttributeCheck());

    checkMessagesVerifier.verify(jspSourceCode.getIssues())
      .noMore();
  }
}
