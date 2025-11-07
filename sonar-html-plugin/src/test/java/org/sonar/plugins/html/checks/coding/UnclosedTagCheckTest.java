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

class UnclosedTagCheckTest {

  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  void detected() {
    UnclosedTagCheck check = new UnclosedTagCheck();
    assertThat(check.ignoreTags).isEqualTo(
      "HTML,HEAD,BODY,P,DT,DD,LI,OPTION,THEAD,TH,TBODY,TR,TD,TFOOT,COLGROUP,IMG,INPUT,BR,HR,FRAME,AREA,BASE,BASEFONT,COL,ISINDEX,LINK,META,PARAM");
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/UnclosedTagCheck/UnclosedTagCheck.html"), check);

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLocation(10, 0, 10, 5).withMessage("The tag \"foo\" has no corresponding closing tag.")
      .noMore();
  }

  @Test
  void custom() {
    UnclosedTagCheck check = new UnclosedTagCheck();
    check.ignoreTags = "html,foo";

    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/UnclosedTagCheck/UnclosedTagCheck.html"), check);

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(4).withMessage("The tag \"li\" has no corresponding closing tag.")
      .next().atLine(7).withMessage("The tag \"br\" has no corresponding closing tag.")
      .next().atLine(15).withMessage("The tag \"li\" has no corresponding closing tag.");
  }

  @Test
  void empty_file() {
    UnclosedTagCheck check = new UnclosedTagCheck();

    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/empty-file.html"), check);
    checkMessagesVerifier.verify(sourceCode.getIssues()).noMore();

    sourceCode = TestHelper.scan(new File("src/test/resources/checks/empty-file.html"), check);
    checkMessagesVerifier.verify(sourceCode.getIssues()).noMore();
  }

  @Test
  void cshtml_are_ignored_by_the_rule() {
    UnclosedTagCheck check = new UnclosedTagCheck();
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/UnclosedTagCheck/UnclosedTagCheck.cshtml"), check);

    checkMessagesVerifier.verify(sourceCode.getIssues()).noMore();
  }

}
