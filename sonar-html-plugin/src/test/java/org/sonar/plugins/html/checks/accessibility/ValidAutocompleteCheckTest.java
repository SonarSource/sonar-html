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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

import java.io.File;


class ValidAutocompleteCheckTest {
  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @ParameterizedTest
  @ValueSource(strings = {
    "cc-exp",
    "cc-exp-month",
    "new-password",
    "street-address",
    "url",
    "username"
  })
  void html(String file) {
    HtmlSourceCode sourceCode = TestHelper.scan(new File(String.format("src/test/resources/checks/DomElementsShouldUseAutocompleteAttributeCorrectlyCheck/%s.html", file)), new ValidAutocompleteCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(1).withMessage("DOM elements should use the \"autocomplete\" attribute correctly.")
      .next().atLine(2)
      .next().atLine(3);
  }

  @Test
  void bday() {
    ValidAutocompleteCheck check = new ValidAutocompleteCheck();
    HtmlSourceCode htmlSourceCode = TestHelper.scan(new File("src/test/resources/checks/DomElementsShouldUseAutocompleteAttributeCorrectlyCheck/bday/file.html"), check);

    checkMessagesVerifier.verify(htmlSourceCode.getIssues())
      .next().atLine(1).withMessage("DOM elements should use the \"autocomplete\" attribute correctly.")
      .next().atLine(2)
      .next().atLine(3);

    HtmlSourceCode jspSourceCode = TestHelper.scan(new File("src/test/resources/checks/DomElementsShouldUseAutocompleteAttributeCorrectlyCheck/bday/file.jsp"), check);

    checkMessagesVerifier.verify(jspSourceCode.getIssues());

    HtmlSourceCode phtmlSourceCode = TestHelper.scan(new File("src/test/resources/checks/DomElementsShouldUseAutocompleteAttributeCorrectlyCheck/bday/file.phtml"), check);

    checkMessagesVerifier.verify(phtmlSourceCode.getIssues());

    HtmlSourceCode vueSourceCode = TestHelper.scan(new File("src/test/resources/checks/DomElementsShouldUseAutocompleteAttributeCorrectlyCheck/bday/file.vue"), check);

    checkMessagesVerifier.verify(vueSourceCode.getIssues())
      .next().atLine(2).withMessage("DOM elements should use the \"autocomplete\" attribute correctly.")
      .next().atLine(3)
      .next().atLine(4);
  }

  @Test
  void on() {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/DomElementsShouldUseAutocompleteAttributeCorrectlyCheck/on.html"), new ValidAutocompleteCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(1).withMessage("DOM elements should use the \"autocomplete\" attribute correctly.");
  }

  @Test
  void tel() {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/DomElementsShouldUseAutocompleteAttributeCorrectlyCheck/tel.html"), new ValidAutocompleteCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(1).withMessage("DOM elements should use the \"autocomplete\" attribute correctly.")
      .next().atLine(2)
      .next().atLine(3)
      .next().atLine(4);
  }

  @Test
  void withContext() {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/DomElementsShouldUseAutocompleteAttributeCorrectlyCheck/with-context.html"), new ValidAutocompleteCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(1).withMessage("DOM elements should use the \"autocomplete\" attribute correctly.")
      .next().atLine(2);
  }

  @Test
  void withWebauthn() {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/DomElementsShouldUseAutocompleteAttributeCorrectlyCheck/with-webauthn.html"), new ValidAutocompleteCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(1).withMessage("DOM elements should use the \"autocomplete\" attribute correctly.")
      .next().atLine(2)
      .next().atLine(3)
      .next().atLine(4)
      .next().atLine(5);
  }
}
