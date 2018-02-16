/*
 * SonarWeb :: SonarQube Plugin
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

import org.junit.Rule;
import org.junit.Test;
import org.sonar.plugins.web.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.web.checks.TestHelper;
import org.sonar.plugins.web.visitor.WebSourceCode;

import java.io.File;

import static org.fest.assertions.Assertions.assertThat;

public class FileLengthCheckTest {

  @Rule
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  public void detected() {
    assertThat(new FileLengthCheck().maxLength).isEqualTo(1000);
  }

  @Test
  public void custom() {
    FileLengthCheck check = new FileLengthCheck();
    check.maxLength = 3;

    WebSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/FileLengthCheck.html"), check);

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(null).withMessage("Current file has 5 lines, which is greater than 3 authorized. Split it into smaller files.");
  }

  @Test
  public void custom_ok() {
    FileLengthCheck check = new FileLengthCheck();
    check.maxLength = 5;

    WebSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/FileLengthCheck.html"), check);

    checkMessagesVerifier.verify(sourceCode.getIssues());
  }

}
