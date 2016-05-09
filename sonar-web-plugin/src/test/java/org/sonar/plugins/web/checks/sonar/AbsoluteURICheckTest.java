/*
 * SonarSource :: Web :: Sonar Plugin
 * Copyright (c) 2010-2016 SonarSource SA and Matthijs Galesloot
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
package org.sonar.plugins.web.checks.sonar;

import java.io.File;

import org.junit.Rule;
import org.junit.Test;
import org.sonar.plugins.web.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.web.checks.TestHelper;
import org.sonar.plugins.web.visitor.WebSourceCode;

public class AbsoluteURICheckTest {

  @Rule
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  public void detected() {
    WebSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/absoluteURICheck.html"), new AbsoluteURICheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(3).withMessage("Replace this absolute URI \"href\" with a relative one, or move this absolute URI to a configuration file.")
      .next().atLine(4).withMessage("Replace this absolute URI \"href\" with a relative one, or move this absolute URI to a configuration file.")
      .next().atLine(7).withMessage("Replace this absolute URI \"src\" with a relative one, or move this absolute URI to a configuration file.")
      .next().atLine(8).withMessage("Replace this absolute URI \"src\" with a relative one, or move this absolute URI to a configuration file.");
  }

  @Test
  public void custom() {
    AbsoluteURICheck check = new AbsoluteURICheck();
    check.attributes = "img.src";

    WebSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/absoluteURICheck.html"), check);

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(7).withMessage("Replace this absolute URI \"src\" with a relative one, or move this absolute URI to a configuration file.")
      .next().atLine(8).withMessage("Replace this absolute URI \"src\" with a relative one, or move this absolute URI to a configuration file.");
  }

}
