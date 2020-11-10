/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (c) 2010-2020 SonarSource SA and Matthijs Galesloot
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
import org.junit.Rule;
import org.junit.Test;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

import static org.fest.assertions.Assertions.assertThat;

public class UnclosedTagCheckTest {

  @Rule
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  public void detected() {
    UnclosedTagCheck check = new UnclosedTagCheck();
    assertThat(check.ignoreTags).isEqualTo(
      "HTML,HEAD,BODY,P,DT,DD,LI,OPTION,THEAD,TH,TBODY,TR,TD,TFOOT,COLGROUP,IMG,INPUT,BR,HR,FRAME,AREA,BASE,BASEFONT,COL,ISINDEX,LINK,META,PARAM");
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/UnclosedTagCheck/UnclosedTagCheck.html"), check);

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLocation(10, 0, 10, 5).withMessage("The tag \"foo\" has no corresponding closing tag.")
      .noMore();
  }

  @Test
  public void custom() {
    UnclosedTagCheck check = new UnclosedTagCheck();
    check.ignoreTags = "html,foo";

    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/UnclosedTagCheck/UnclosedTagCheck.html"), check);

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(4).withMessage("The tag \"li\" has no corresponding closing tag.")
      .next().atLine(7).withMessage("The tag \"br\" has no corresponding closing tag.")
      .next().atLine(15).withMessage("The tag \"li\" has no corresponding closing tag.");
  }

  @Test
  public void empty_file() {
    UnclosedTagCheck check = new UnclosedTagCheck();

    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/empty-file.html"), check);
    checkMessagesVerifier.verify(sourceCode.getIssues()).noMore();

    sourceCode = TestHelper.scan(new File("src/test/resources/checks/empty-file.html"), check);
    checkMessagesVerifier.verify(sourceCode.getIssues()).noMore();
  }

  @Test
  public void cshtml_are_ignored_by_the_rule() {
    UnclosedTagCheck check = new UnclosedTagCheck();
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/UnclosedTagCheck/UnclosedTagCheck.cshtml"), check);

    checkMessagesVerifier.verify(sourceCode.getIssues()).noMore();
  }

}
