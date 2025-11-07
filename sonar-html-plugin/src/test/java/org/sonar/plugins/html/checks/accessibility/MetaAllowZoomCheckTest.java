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

import java.io.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

class MetaAllowZoomCheckTest {

  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  void html() {
    HtmlSourceCode sourceCode = TestHelper.scan(
            new File("src/test/resources/checks/MetaAllowZoomCheck.html"),
            new MetaAllowZoomCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
            .next().atLine(12).withMessage("Meta viewport disables zoom via user-scalable=no.")
            .next().atLine(13).withMessage("Meta viewport disables zoom via user-scalable=no.")
            .next().atLine(14).withMessage("Meta viewport limits zoom with maximum-scale < 2.")
            .next().atLine(15).withMessage("Meta viewport disables zoom via user-scalable=no.")
            .noMore();
  }
}
