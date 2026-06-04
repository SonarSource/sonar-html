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
package org.sonar.plugins.html.checks.accessibility;

import java.io.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

class MisplacedLegendCheckTest {
  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  private static final String MESSAGE = "Move this <legend> element to be a direct child of a <fieldset> or <optgroup> element.";

  @Test
  void html() {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/MisplacedLegendCheck/test.html"),
      new MisplacedLegendCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(15).withMessage(MESSAGE)
      .next().atLine(22)
      .next().atLine(30)
      .next().atLine(37)
      .next().atLine(41)
      .noMore();
  }

  @Test
  void vue() {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/MisplacedLegendCheck/test.vue"),
      new MisplacedLegendCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(18).withMessage(MESSAGE)
      .noMore();
  }

  @Test
  void razorSection() {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/MisplacedLegendCheck/test.cshtml"),
      new MisplacedLegendCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(5).withMessage(MESSAGE)
      .next().atLine(8).withMessage(MESSAGE)
      .noMore();
  }
}
