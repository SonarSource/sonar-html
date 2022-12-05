/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (c) 2010-2022 SonarSource SA and Matthijs Galesloot
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
package org.sonar.plugins.html.checks.scripting;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.junit.Rule;
import org.junit.Test;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

public class UnifiedExpressionCheckTest {

  @Rule
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
