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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

class PreferTagOverRoleCheckTest {
  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  void html() {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/PreferTagOverRoleCheck.html"),
      new PreferTagOverRoleCheck());
    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(1).withMessage("Use <input> instead of the checkbox role to ensure accessibility across all devices.")
      .next().atLine(2).withMessage("Use <button> or <input> instead of the button role to ensure accessibility across all devices.")
      .next().atLine(3).withMessage("Use <h1> or <h2> or <h3> or <h4> or <h5> or <h6> instead of the heading role to ensure accessibility across all devices.")
      .next().atLine(4).withMessage("Use <a> or <area> instead of the link role to ensure accessibility across all devices.")
      .next().atLine(5).withMessage("Use <tbody> or <tfoot> or <thead> instead of the rowgroup role to ensure accessibility across all devices.")
      .next().atLine(6).withMessage("Use <input> instead of the checkbox role to ensure accessibility across all devices.")
      .next().atLine(7).withMessage("Use <header> instead of the banner role to ensure accessibility across all devices.")
      .next().atLine(8).withMessage("Use <output> instead of the status role to ensure accessibility across all devices.")
      .next().atLine(9).withMessage("Use <output> instead of the status role to ensure accessibility across all devices.")
      .next().atLine(10).withMessage("Use <a> or <area> instead of the link role to ensure accessibility across all devices.")
      .consume();
  }

  @Test
  void allowedRoles() {
    var check = new PreferTagOverRoleCheck();
    check.allowedRoles = " LINK , status ";

    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/PreferTagOverRoleCheck.html"),
      check);
    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(1).withMessage("Use <input> instead of the checkbox role to ensure accessibility across all devices.")
      .next().atLine(2).withMessage("Use <button> or <input> instead of the button role to ensure accessibility across all devices.")
      .next().atLine(3).withMessage("Use <h1> or <h2> or <h3> or <h4> or <h5> or <h6> instead of the heading role to ensure accessibility across all devices.")
      .next().atLine(5).withMessage("Use <tbody> or <tfoot> or <thead> instead of the rowgroup role to ensure accessibility across all devices.")
      .next().atLine(6).withMessage("Use <input> instead of the checkbox role to ensure accessibility across all devices.")
      .next().atLine(7).withMessage("Use <header> instead of the banner role to ensure accessibility across all devices.")
      .consume();
  }

  @Test
  void allowedRolesOnlyMatchesFirstApplicableToken() {
    var check = new PreferTagOverRoleCheck();
    check.allowedRoles = "status";

    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/PreferTagOverRoleCheckFirstApplicableToken.html"),
      check);
    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(1).withMessage("Use <input> instead of the checkbox role to ensure accessibility across all devices.")
      .next().atLine(3).withMessage("Use <a> or <area> instead of the link role to ensure accessibility across all devices.")
      .consume();
  }

  @Test
  void firstApplicableRoleWithoutEquivalentHtmlTagIsIgnored() {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/PreferTagOverRoleCheckFirstApplicableToken.html"),
      new PreferTagOverRoleCheck());
    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(1).withMessage("Use <input> instead of the checkbox role to ensure accessibility across all devices.")
      .next().atLine(2).withMessage("Use <output> instead of the status role to ensure accessibility across all devices.")
      .next().atLine(3).withMessage("Use <a> or <area> instead of the link role to ensure accessibility across all devices.")
      .consume();
  }

  @Test
  void unknownAllowedRolesAreReportedAsAnalysisWarning() {
    var check = new PreferTagOverRoleCheck();
    check.allowedRoles = "checkbocx, status, totallyMadeUp";

    TestHelper.scan(
      new File("src/test/resources/checks/PreferTagOverRoleCheck.html"),
      check);

    assertThat(check.collectAnalysisWarnings())
      .singleElement()
      .asString()
      .contains("checkbocx", "totallyMadeUp")
      .doesNotContain("status");
  }

  @Test
  void blankAllowedRolesProducesNoWarning() {
    var check = new PreferTagOverRoleCheck();
    check.allowedRoles = "";

    TestHelper.scan(
      new File("src/test/resources/checks/PreferTagOverRoleCheck.html"),
      check);

    assertThat(check.collectAnalysisWarnings()).isEmpty();
  }
}
