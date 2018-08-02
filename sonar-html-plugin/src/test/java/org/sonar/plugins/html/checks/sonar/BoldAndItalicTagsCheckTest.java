/*
 * SonarHTML :: SonarQube Plugin
 * Copyright (c) 2010-2018 SonarSource SA and Matthijs Galesloot
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

public class BoldAndItalicTagsCheckTest {

  @Rule
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  public void detected() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/BoldAndItalicTagsCheck.html"), new BoldAndItalicTagsCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(1).withMessage("Replace this <b> tag by <strong>.")
        .next().atLine(5).withMessage("Replace this <i> tag by <em>.")
        .next().atLine(7).withMessage("Replace this <B> tag by <strong>.")
        .next().atLine(11).withMessage("Replace this <i> tag by <em>.")
        .next().atLine(17).withMessage("Replace this <i> tag by <em>.")
        .next().atLine(19).withMessage("Replace this <i> tag by <em>.")
        .next().atLine(21).withMessage("Replace this <i> tag by <em>.");
  }

}
