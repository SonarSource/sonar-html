/*
 * SonarHTML :: SonarQube Plugin
 * Copyright (c) 2010-2019 SonarSource SA and Matthijs Galesloot
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

import java.io.File;

import org.junit.Rule;
import org.junit.Test;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

public class LayoutTableWithSemanticMarkupCheckTest {

  @Rule
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
