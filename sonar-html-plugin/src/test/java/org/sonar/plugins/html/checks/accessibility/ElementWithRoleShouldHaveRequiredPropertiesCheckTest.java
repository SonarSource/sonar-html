/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (c) 2010-2024 SonarSource SA and Matthijs Galesloot
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
package org.sonar.plugins.html.checks.accessibility;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

import java.io.File;

class ElementWithRoleShouldHaveRequiredPropertiesCheckTest {
  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  void validHTML() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/ElementWithRoleShouldHaveRequiredPropertiesCheckTest/valid.html"),
      new ElementWithRoleShouldHaveRequiredPropertiesCheck()
    );

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .noMore();
  }

  @Test
  void invalidHTML() throws Exception {
    HtmlSourceCode invalidSourceCode = TestHelper.scan(
      new File("src/test/resources/checks/ElementWithRoleShouldHaveRequiredPropertiesCheckTest/invalid.html"),
      new ElementWithRoleShouldHaveRequiredPropertiesCheck()
    );

    checkMessagesVerifier.verify(invalidSourceCode.getIssues())
      .next().atLine(2).withMessage("The attribute \"aria-checked\" is required by the role \"checkbox\".")
      .next().atLine(3).withMessage("The attribute \"aria-controls\" is required by the role \"combobox\".")
      .next().atLine(3).withMessage("The attribute \"aria-expanded\" is required by the role \"combobox\".")
      .next().atLine(4).withMessage("The attribute \"aria-level\" is required by the role \"heading\".")
      .next().atLine(5).withMessage("The attribute \"aria-checked\" is required by the role \"menuitemcheckbox\".")
      .next().atLine(6).withMessage("The attribute \"aria-checked\" is required by the role \"menuitemradio\".")
      .next().atLine(7).withMessage("The attribute \"aria-valuenow\" is required by the role \"meter\".")
      .next().atLine(8).withMessage("The attribute \"aria-selected\" is required by the role \"option\".")
      .next().atLine(9).withMessage("The attribute \"aria-checked\" is required by the role \"radio\".")
      .next().atLine(10).withMessage("The attribute \"aria-controls\" is required by the role \"scrollbar\".")
      .next().atLine(10).withMessage("The attribute \"aria-valuenow\" is required by the role \"scrollbar\".")
      .next().atLine(11).withMessage("The attribute \"aria-valuenow\" is required by the role \"slider\".")
      .next().atLine(12).withMessage("The attribute \"aria-checked\" is required by the role \"switch\".")
      .next().atLine(13).withMessage("The attribute \"aria-selected\" is required by the role \"treeitem\".")
      .next().atLine(14).withMessage("The attribute \"aria-checked\" is required by the role \"radio\".")
      .next().atLine(15).withMessage("The attribute \"aria-selected\" is required by the role \"option\".")
    ;
  }
}
