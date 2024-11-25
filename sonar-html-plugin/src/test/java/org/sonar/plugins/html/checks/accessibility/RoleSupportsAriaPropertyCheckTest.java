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

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

class RoleSupportsAriaPropertyCheckTest {
  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  void html() {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/RoleSupportsAriaPropertyCheck.html"),
      new RoleSupportsAriaPropertyCheck());
    var issues = sourceCode.getIssues();
    assertThat(issues).hasSize(40);
    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(1).withMessage("The attribute aria-selected is not supported by the role button. This role is implicit on the element button.")
      .next().atLine(3).withMessage("The attribute aria-selected is not supported by the role button.")
      .next().atLine(5).withMessage("The attribute aria-selected is not supported by the role button or paragraph.")
      .consume();
  }
}