/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (c) 2010-2022 SonarSource SA and Matthijs Galesloot
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

public class IndistinguishableSimilarElementsCheckTest {
  
  @Rule
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  public void singleNavAside() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/IndistinguishableSimilarElementsCheck/SingleNavAside.html"), new IndistinguishableSimilarElementsCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues());
  }

  @Test
  public void multipleNavAside() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/IndistinguishableSimilarElementsCheck/MultipleNavAside.html"), new IndistinguishableSimilarElementsCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLocation(1, 0, 1, 5).withMessage("Add an \"aria-label\" or \"aria-labbelledby\" attribute to this element.")
        .next().atLine(4)
        .next().atLine(13)
        .next().atLine(16)
        .next().atLine(25)
        .next().atLine(28)
        .next().atLine(31)
        .next().atLine(55);
  }
}
