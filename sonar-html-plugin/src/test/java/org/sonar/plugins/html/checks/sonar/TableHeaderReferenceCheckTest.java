/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (c) 2010-2023 SonarSource SA and Matthijs Galesloot
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
package org.sonar.plugins.html.checks.sonar;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

import java.io.File;

public class TableHeaderReferenceCheckTest {
  
  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  public void detected() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/TableHeaderReferenceCheck.html"), new TableHeaderReferenceCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLocation(35, 4, 35, 22).withMessage("id \"bar\" in \"headers\" does not reference any <th> header.")
        .next().atLine(56).withMessage("id \"bar\" in \"headers\" does not reference any <th> header.")
        .next().atLine(78).withMessage("id \"oof\" in \"headers\" does not reference any <th> header.")
        .next().atLine(89).withMessage("id \"bar\" in \"headers\" reference the header of another column/row.")
        .next().atLine(90).withMessage("id \"foo\" in \"headers\" reference the header of another column/row.")
        .next().atLine(108).withMessage("id \"oof\" in \"headers\" does not reference any <th> header.")
        .next().atLine(119).withMessage("id \"bar\" in \"headers\" reference the header of another column/row.")
        .next().atLine(123).withMessage("id \"foo\" in \"headers\" reference the header of another column/row.")
        .next().atLine(152).withMessage("id \"bar\" in \"headers\" does not reference any <th> header.")
        .next().atLine(220).withMessage("id \"bar\" in \"headers\" does not reference any <th> header.")
        .next().atLine(227).withMessage("id \"bar\" in \"headers\" does not reference any <th> header.")
        .next().atLine(275).withMessage("id \"foo\" in \"headers\" does not reference any <th> header.")
        .next().atLine(281).withMessage("id \"foo\" in \"headers\" does not reference any <th> header.");
  }
}
