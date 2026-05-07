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
package org.sonar.plugins.html.checks.sonar;

import java.io.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

class AllowedLangAttributeCheckTest {

  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  void detected() {
    AllowedLangAttributeCheck check = new AllowedLangAttributeCheck();
    check.languages = "en,fr,noISOlang";

    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/AllowedLangAttributeCheck/test.html"),
      check);

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(3).withMessage(AllowedLangAttributeCheck.ALLOWED_LANG_MESSAGE)
      .next().atLine(11).withMessage(AllowedLangAttributeCheck.ALLOWED_LANG_MESSAGE)
      .next().atLine(13).withMessage(AllowedLangAttributeCheck.ALLOWED_LANG_MESSAGE)
      .noMore();
  }

  @Test
  void emptyAllowlistDoesNotFlagAnything() {
    AllowedLangAttributeCheck check = new AllowedLangAttributeCheck();
    check.languages = "";

    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/AllowedLangAttributeCheck/test.html"),
      check);

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .noMore();
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "src/test/resources/checks/LangAttributeCheckJspEl.html",
    "src/test/resources/checks/LangAttributeCheckErb.html.erb",
    "src/test/resources/checks/LangAttributeCheckSvelteKit.html"
  })
  void dynamicLangValuesShouldNotRaiseIssues(String file) {
    AllowedLangAttributeCheck check = new AllowedLangAttributeCheck();
    check.languages = "en,fr";

    HtmlSourceCode sourceCode = TestHelper.scan(new File(file), check);

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .noMore();
  }
}
