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
package org.sonar.plugins.web.checks.coding;

import java.io.File;

import org.junit.Rule;
import org.junit.Test;
import org.sonar.plugins.web.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.web.checks.TestHelper;
import org.sonar.plugins.web.visitor.HtmlSourceCode;

public class MaxLineLengthCheckTest {

  @Rule
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  public void test() {
    HtmlSourceCode file = TestHelper.scan(new File("src/test/resources/checks/MaxLineLengthCheck.html"), new MaxLineLengthCheck());
    checkMessagesVerifier.verify(file.getIssues())
        .next().atLine(2).withMessage("Split this 121 characters long line (which is greater than 120 authorized).")
        .next().atLine(3).withMessage("Split this 122 characters long line (which is greater than 120 authorized).");
  }

  @Test
  public void custom() {
    MaxLineLengthCheck check = new MaxLineLengthCheck();
    check.maxLength = 40;

    HtmlSourceCode file = TestHelper.scan(new File("src/test/resources/checks/MaxLineLengthCheck.html"), check);
    checkMessagesVerifier.verify(file.getIssues())
        .next().atLine(1)
        .next().atLine(2)
        .next().atLine(3)
        .next().atLine(6).withMessage("Split this 41 characters long line (which is greater than 40 authorized).");
  }

}
