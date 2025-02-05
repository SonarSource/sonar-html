/*
 * SonarQube HTML
 * Copyright (C) 2010-2025 SonarSource SA
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
package org.sonar.plugins.html.checks.sonar;

import java.io.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

public class LayoutTableWithSemanticMarkupCheckTest {

  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  public void detected() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/LayoutTableWithSemanticMarkupCheck.html"), new LayoutTableWithSemanticMarkupCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(17).withMessage("Remove this \"summary\" attribute")
        .next().atLocation(18, 2, 18, 11).withMessage("Remove this \"caption\" element")
        .next().atLine(20).withMessage("Remove this \"th\" element")
        .next().atLine(34).withMessage("Remove this \"headers\" attribute")
        .next().atLine(35).withMessage("Remove this \"scope\" attribute")
        .next().atLine(39);
  }
}
