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

import java.io.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

class AriaProptypesCheckTest {

  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  void html() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/AriaProptypesCheck.html"),
      new AriaProptypesCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(10).withMessage("The value of the attribute \"aria-hidden\" must be a boolean.")
      .next().atLine(14).withMessage("The value of the attribute \"aria-label\" must be a string.")
      .next().atLine(15)
      .next().atLine(21).withMessage("The value of the attribute \"aria-checked\" must be a boolean or the string \"mixed\".")
      .next().atLine(27).withMessage("The value of the attribute \"aria-valuemax\" must be a number.")
      .next().atLine(33).withMessage("The value of the attribute \"aria-posinset\" must be a integer.")
      .next().atLine(38).withMessage("The value of the attribute \"aria-orientation\" must be a single token from the following: horizontal, vertical, undefined.")
      .next().atLine(43).withMessage("The value of the attribute \"aria-dropeffect\" must be a list of one or more tokens from the following: copy, move, link, execute, none, popup.")
      .next().atLine(44)
      .next().atLine(49).withMessage("The value of the attribute \"aria-controls\" must be a list of strings that represent DOM element IDs (idlist).")
      .next().atLine(53).withMessage("The value of the attribute \"aria-details\" must be a string that represents a DOM element ID.")
      .noMore();
  }
}
