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

class NoInteractiveElementToNoninteractiveRoleCheckTest {
  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  void valid() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/NoInteractiveElementToNoninteractiveRoleCheck/valid.html"),
      new NoInteractiveElementToNoninteractiveRoleCheck());

  checkMessagesVerifier.verify(sourceCode.getIssues())
      .noMore();
  }

  @Test
  void invalid() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/NoInteractiveElementToNoninteractiveRoleCheck/invalid.html"),
      new NoInteractiveElementToNoninteractiveRoleCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(2).withMessage("Interactive elements should not be assigned non-interactive roles.")
      .next().atLine(3)
      .next().atLine(5)
      .next().atLine(6)
      .next().atLine(7)
      .next().atLine(8)
      .next().atLine(9)
      .next().atLine(10)
      .next().atLine(11)
      .next().atLine(12)
      .next().atLine(13)
      .next().atLine(14)
      .next().atLine(15)
      .next().atLine(16)
      .next().atLine(17)
      .next().atLine(18)
      .next().atLine(19)
      .next().atLine(20)
      .next().atLine(21)
      .next().atLine(22)
      .next().atLine(23)
      .next().atLine(24)
      .next().atLine(25)
      .next().atLine(26)
      .next().atLine(27)
      .next().atLine(28)
      .next().atLine(30)
      .next().atLine(31)
      .next().atLine(32)
      .next().atLine(33)
      .next().atLine(34)
      .next().atLine(36)
      .next().atLine(37)
      .next().atLine(39)
      .next().atLine(40)
      .next().atLine(41)
      .next().atLine(42)
      .next().atLine(43)
      .next().atLine(44)
      .next().atLine(45)
      .next().atLine(46)
      .next().atLine(47)
      .next().atLine(48)
      .next().atLine(49)
      .next().atLine(50)
      .next().atLine(51)
      .next().atLine(52)
      .next().atLine(53)
      .next().atLine(54)
      .next().atLine(55)
      .next().atLine(56)
      .next().atLine(57)
      .next().atLine(58)
      .next().atLine(59)
      .next().atLine(60)
      .next().atLine(61)
      .next().atLine(63)
      .next().atLine(64)
      .next().atLine(65)
      .next().atLine(66)
      .next().atLine(67)
      .noMore();

  }
}
