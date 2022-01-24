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

public class TableHeaderHasIdOrScopeCheckTest {

  @Rule
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  public void detected() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/TableHeaderHasIdOrScopeCheck.html"), new TableHeaderHasIdOrScopeCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLocation(41, 8, 41, 12).withMessage("Add either an 'id' or a 'scope' attribute to this <th> tag.")
      .next().atLine(42).withMessage("Add either an 'id' or a 'scope' attribute to this <th> tag.")
      .next().atLine(70)
      .next().atLine(76)
      .next().atLine(101)
      .next().atLine(102)
      .next().atLine(103)
      .next().atLine(106)
      .next().atLine(111)
      .next().atLine(140)
      .next().atLine(141)
      .next().atLine(144)
      .next().atLine(149)
      .next().atLine(177)
      .next().atLine(178)
      .next().atLine(186)
      .next().atLine(209)
      .next().atLine(210)
      .next().atLine(214)
      .next().atLine(238)
      .next().atLine(239)
      .next().atLine(275)
      .next().atLine(285)
      .next().atLine(320)
      .next().atLine(327)
      .next().atLine(334)
      .next().atLine(380)
      .next().atLine(387)
      .next().atLine(387)
      .next().atLine(392)
      .next().atLine(393)
      .next().atLine(408)
      .next().atLine(415)
      .next().atLine(415)
      .next().atLine(420)
      .next().atLine(421)
      .next().atLine(494)
      .next().atLine(495)
      .next().atLine(506)
      .next().atLine(507)
      .next().atLine(519)
      .next().atLine(520)
      .next().atLine(570)
      .next().atLine(574)
      .next().atLine(605)
      .next().atLine(617)
      .next().atLine(621)
      .next().atLine(623)
    ;
  }

}
