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
package org.sonar.plugins.html.checks.coding;

import java.io.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

import static org.assertj.core.api.Assertions.assertThat;

class InternationalizationCheckTest {

  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  void detected() {
    assertThat(new InternationalizationCheck().attributes).isEqualTo("outputLabel.value, outputText.value");
  }

  @Test
  void custom() {
    InternationalizationCheck check = new InternationalizationCheck();
    check.attributes = "outputLabel.value";

    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/InternationalizationCheck.html"), check);

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLocation(1, 13, 1, 16).withMessage("Define this label in the resource bundle.")
      .next().atLocation(2, 0, 2, 25).withMessage("Define this label in the resource bundle.")
      .next().atLine(9).withMessage("Define this label in the resource bundle.")
      .next().atLine(10).withMessage("Define this label in the resource bundle.")
      .next().atLine(11).withMessage("Define this label in the resource bundle.");
  }

  @Test
  void should_not_raise_issue_with_bom() {
    InternationalizationCheck check = new InternationalizationCheck();

    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/InternationalizationCheck_UTF8.html"), check);
    checkMessagesVerifier.verify(sourceCode.getIssues()).noMore();

    sourceCode = TestHelper.scan(new File("src/test/resources/checks/InternationalizationCheck_UTF8WithBom.html"), check);
    checkMessagesVerifier.verify(sourceCode.getIssues()).noMore();
  }


  @Test
  void custom2() {
    InternationalizationCheck check = new InternationalizationCheck();
    check.attributes = "";

    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/InternationalizationCheck.html"), check);

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(1).withMessage("Define this label in the resource bundle.")
      .next().atLine(9).withMessage("Define this label in the resource bundle.")
      .next().atLine(11).withMessage("Define this label in the resource bundle.");
  }

  @Test
  void regexIgnore1() {
    InternationalizationCheck check = new InternationalizationCheck();
    check.attributes = "outputLabel.value";
    check.ignoredContentRegex = ".*cDe.*";
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/InternationalizationCheck.html"), check);
    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(1).withMessage("Define this label in the resource bundle.")
      .next().atLine(2).withMessage("Define this label in the resource bundle.");
  }
  @Test
  void regexIgnore2() {
    InternationalizationCheck check = new InternationalizationCheck();
    check.attributes = "";
    check.ignoredContentRegex = ".*cDe.*";
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/InternationalizationCheck.html"), check);
    checkMessagesVerifier.verify(sourceCode.getIssues()).next().atLine(1).withMessage("Define this label in the resource bundle.");
  }

}
