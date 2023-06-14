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


import java.io.File;

import org.junit.Rule;
import org.junit.Test;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

public class MouseEventWithoutKeyboardEquivalentCheckTest {

  @Rule
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  public void detected() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/MouseEventWithoutKeyboardEquivalentCheck.html"), new MouseEventWithoutKeyboardEquivalentCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLocation(1, 0, 1, 32).withMessage("Add a 'onFocus' attribute to this <A> tag.")
        .next().atLine(2).withMessage("Add a 'onBlur' attribute to this <a> tag.")
        .next().atLine(14).withMessage("Add a 'onFocus' attribute to this <a> tag.")
        .next().atLine(15)
        .next().atLine(24)
        .next().atLine(25)
        .next().atLine(35)
        .next().atLine(36)
        .next().atLine(46)
        .next().atLine(51)
        .next().atLine(53)
        .next().atLine(54)
        .next().atLine(58).withMessage("Add a 'onKeyPress|onKeyDown|onKeyUp' attribute to this <div> tag.")
        .next().atLine(59)
        .next().atLine(65)
        .next().atLine(68)
        .next().atLine(70)
        .next().atLine(71);
  }

}
