/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (c) 2010-2024 SonarSource SA and Matthijs Galesloot
 * sonarqube@googlegroups.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
