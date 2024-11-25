/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (C) 2010-2024 SonarSource SA and Matthijs Galesloot
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

class ImgRedundantAltCheckTest {

  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  void test() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/ImgRedundantAltCheck.html"),
      new ImgRedundantAltCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(1).withMessage(
        "Remove redundant words \"photo\", \"picture\" from the \"alt\" attribute of your \"img\" tag.")
      .next().atLine(2).withMessage(
        "Remove redundant word \"photo\" from the \"alt\" attribute of your \"img\" tag.")
      .next().atLine(3)
      .next().atLine(4)
      .noMore();
  }
}
