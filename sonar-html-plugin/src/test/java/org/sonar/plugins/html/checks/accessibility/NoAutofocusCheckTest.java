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

class NoAutofocusCheckTest {
  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  void valid() {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/NoAutofocusCheck/valid.html"),
      new NoAutofocusCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .noMore();
  }

  @Test
  void invalid() {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/NoAutofocusCheck/invalid.html"),
      new NoAutofocusCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(1).withMessage("Remove this \"autofocus\" attribute, as it can reduce usability and accessibility for users.")
      .next().atLine(2)
      .next().atLine(3)
      .next().atLine(4)
      .next().atLine(5)
      .next().atLine(7)
      .next().atLine(9)
      .next().atLine(10)
      .next().atLine(11)
      .noMore();
  }
}
