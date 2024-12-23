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
package org.sonar.plugins.html.checks.scripting;

import java.io.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

import static org.assertj.core.api.Assertions.assertThat;

public class UnifiedExpressionCheckTest {

  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  public void detected() {
    assertThat(new UnifiedExpressionCheck().functions).isEmpty();
  }

  @Test
  public void custom() {
    UnifiedExpressionCheck check = new UnifiedExpressionCheck();
    check.functions = "myMethod2,myMethod3";

    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/UnifiedExpressionCheck.jsp"), check);

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(2).withMessage("Fix this expression: Unknown function \"myMethod1\".")
      .next().atLine(6).withMessage("Fix this expression: Error Parsing: ${}")
      .next().atLine(10).withMessage("Fix this expression: Error Parsing: ${{'one':1,}");
  }

  @Test
  public void should_not_detect_unknown_functions_with_empty_list() {
    UnifiedExpressionCheck check = new UnifiedExpressionCheck();
    check.functions = "";

    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/UnifiedExpressionCheck.jsp"), check);

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(6)
      .next().atLine(10);
  }

}
