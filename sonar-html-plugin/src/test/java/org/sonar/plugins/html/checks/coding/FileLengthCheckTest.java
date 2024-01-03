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
package org.sonar.plugins.html.checks.coding;

import java.io.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

import static org.assertj.core.api.Assertions.assertThat;

public class FileLengthCheckTest {

  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  public void detected() {
    assertThat(new FileLengthCheck().maxLength).isEqualTo(1000);
  }

  @Test
  public void custom() {
    FileLengthCheck check = new FileLengthCheck();
    check.maxLength = 2;

    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/FileLengthCheck.html"), check);

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(null).withMessage("Current file has 3 lines, which is greater than 2 authorized. Split it into smaller files.");
  }

  @Test
  public void custom_ok() {
    FileLengthCheck check = new FileLengthCheck();
    check.maxLength = 3;

    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/FileLengthCheck.html"), check);

    checkMessagesVerifier.verify(sourceCode.getIssues());
  }

}
