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
package org.sonar.plugins.html.checks.coding;

import java.io.File;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

import static org.assertj.core.api.Assertions.assertThat;

public class InternationalizationCheckTest {

  @Rule
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  public void detected() {
    assertThat(new InternationalizationCheck().attributes).isEqualTo("outputLabel.value, outputText.value");
  }

  @Test
  public void custom() {
    InternationalizationCheck check = new InternationalizationCheck();
    check.attributes = "outputLabel.value";

    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/InternationalizationCheck.html"), check);

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLocation(1, 13, 1, 16).withMessage("Define this label in the resource bundle.")
      .next().atLocation(2, 0, 2, 25).withMessage("Define this label in the resource bundle.")
      .next().atLine(9).withMessage("Define this label in the resource bundle.")
      .next().atLine(10).withMessage("Define this label in the resource bundle.")
      .next().atLine(11).withMessage("Define this label in the resource bundle.");
  }

  @Test
  public void should_not_raise_issue_with_bom() {
    InternationalizationCheck check = new InternationalizationCheck();

    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/InternationalizationCheck_UTF8.html"), check);
    checkMessagesVerifier.verify(sourceCode.getIssues()).noMore();

    sourceCode = TestHelper.scan(new File("src/test/resources/checks/InternationalizationCheck_UTF8WithBom.html"), check);
    checkMessagesVerifier.verify(sourceCode.getIssues()).noMore();
  }


  @Test
  public void custom2() {
    InternationalizationCheck check = new InternationalizationCheck();
    check.attributes = "";

    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/InternationalizationCheck.html"), check);

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(1).withMessage("Define this label in the resource bundle.")
      .next().atLine(9).withMessage("Define this label in the resource bundle.")
      .next().atLine(11).withMessage("Define this label in the resource bundle.");
  }

  @Test
  public void regexIgnore1() {
    InternationalizationCheck check = new InternationalizationCheck();
    check.attributes = "outputLabel.value";
    check.ignoredContentRegex = ".*cDe.*";
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/InternationalizationCheck.html"), check);
    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(1).withMessage("Define this label in the resource bundle.")
      .next().atLine(2).withMessage("Define this label in the resource bundle.");
  }
  @Test
  public void regexIgnore2() {
    InternationalizationCheck check = new InternationalizationCheck();
    check.attributes = "";
    check.ignoredContentRegex = ".*cDe.*";
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/InternationalizationCheck.html"), check);
    checkMessagesVerifier.verify(sourceCode.getIssues()).next().atLine(1).withMessage("Define this label in the resource bundle.");
  }

}
