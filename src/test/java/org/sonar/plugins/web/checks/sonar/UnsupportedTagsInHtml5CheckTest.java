/*
 * SonarQube Web Plugin
 * Copyright (C) 2010 SonarSource and Matthijs Galesloot
 * dev@sonar.codehaus.org
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


import org.sonar.plugins.web.checks.TestHelper;

import org.junit.Rule;
import org.junit.Test;
import org.sonar.plugins.web.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.web.visitor.WebSourceCode;

import java.io.File;

public class UnsupportedTagsInHtml5CheckTest {

  @Rule
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  public void test() throws Exception {
    WebSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/UnsupportedTagsInHtml5Check.html"), new UnsupportedTagsInHtml5Check());

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(1).withMessage("Remove this deprecated \"acronym\" element.")
        .next().atLine(2).withMessage("Remove this deprecated \"applet\" element.")
        .next().atLine(3)
        .next().atLine(4)
        .next().atLine(5)
        .next().atLine(6)
        .next().atLine(7)
        .next().atLine(8)
        .next().atLine(9)
        .next().atLine(10)
        .next().atLine(11)
        .next().atLine(12)
        .next().atLine(13)
        .next().atLine(15).withMessage("Remove this deprecated \"sTrIkE\" element.");
  }

}
