/*
 * SonarQube HTML
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
