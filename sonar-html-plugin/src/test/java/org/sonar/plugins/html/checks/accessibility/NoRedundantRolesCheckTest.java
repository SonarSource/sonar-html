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

import java.io.File;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

class NoRedundantRolesCheckTest {
  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  void html() {
    HtmlSourceCode sourceCode = TestHelper.scan(
        new File("src/test/resources/checks/NoRedundantRolesCheck.html"),
        new NoRedundantRolesCheck());
    System.out.println("sourceCode: " + sourceCode.getIssues());
    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(1)
        .withMessage(
            "The element button has an implicit role of button. Definig this explicitly is redundant and should be avoided.")
        .next().atLine(2)
        .noMore();
  }

  @Test
  void html_with_custom_property() {
    var check = new NoRedundantRolesCheck();
    check.allowedRedundantRoles = "button=button,body=document";
    HtmlSourceCode sourceCode = TestHelper.scan(
        new File("src/test/resources/checks/NoRedundantRolesCheck.html"),
        check);
    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(6).withMessage("The element nav has an implicit role of navigation. Definig this explicitly is redundant and should be avoided.")
      .noMore();
  }

  @Test
  void html_with_invalid_custom_property() {
    var check = new NoRedundantRolesCheck();
    // the second pair is invalid, should be ignored
    check.allowedRedundantRoles = "button=button,body=document=invalid";
    HtmlSourceCode sourceCode = TestHelper.scan(
        new File("src/test/resources/checks/NoRedundantRolesCheck.html"),
        check);
    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(2)
      .next().atLine(6).withMessage("The element nav has an implicit role of navigation. Definig this explicitly is redundant and should be avoided.")
      .noMore();
  }
}
