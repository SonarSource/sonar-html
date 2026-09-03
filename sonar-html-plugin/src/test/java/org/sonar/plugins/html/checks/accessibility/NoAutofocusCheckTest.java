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
      .next().atLine(13)
      .next().atLine(15)
      .next().atLine(16)
      .noMore();
  }

  @Test
  void vueComponentPropShouldNotBeFlagged() {
    // CustomInput / custom-input / Input are Vue components; autofocus there is a prop, not the
    // DOM attribute - Input collides case-insensitively with the native <input> tag but must
    // still be treated as a component
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/NoAutofocusCheck/VueComponents.vue"),
      new NoAutofocusCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      // Only the native <input> should be flagged
      .next().atLine(14)
      .noMore();
  }

  @Test
  void customElementPropInNonVueFileShouldNotBeFlagged() {
    // The kebab-case/unknown-tag exemption is not gated on the .vue extension, but PascalCase is:
    // outside Vue, a known HTML tag typed in caps (e.g. <BUTTON>) is still just the native tag.
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/NoAutofocusCheck/CustomElementInHtmlFile.html"),
      new NoAutofocusCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(6)
      .next().atLine(9)
      .next().atLine(10)
      .noMore();
  }
}
