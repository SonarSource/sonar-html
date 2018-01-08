/*
 * SonarSource :: Web :: Sonar Plugin
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
package org.sonar.plugins.web.checks.header;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.plugins.web.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.web.checks.TestHelper;
import org.sonar.plugins.web.visitor.WebSourceCode;

import java.io.File;

public class HeaderCheckTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Rule
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  public void correct_header() {
    HeaderCheck check = new HeaderCheck();
    check.headerFormat = "<!-- Copyright foo -->";

    WebSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/HeaderCheck/CorrectHeader.html"), check);

    checkMessagesVerifier.verify(sourceCode.getIssues());
  }

  @Test
  public void missing_header() {
    HeaderCheck check = new HeaderCheck();
    check.headerFormat = "<!-- Copyright foo -->";

    WebSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/HeaderCheck/MissingHeader.html"), check);

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(null).withMessage("Add or update the header of this file.");
  }

  @Test
  public void misspelled_header() {
    HeaderCheck check = new HeaderCheck();
    check.headerFormat = "<!-- Copyright foo -->";

    WebSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/HeaderCheck/MisspelledHeader.html"), check);

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(null).withMessage("Add or update the header of this file.");
  }

  @Test
  public void regex1() {
    HeaderCheck check = new HeaderCheck();
    check.headerFormat = "<!-- copyright \\d{4}\\n  mycompany -->";
    check.isRegularExpression = true;

    WebSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/HeaderCheck/Regex1.html"), check);

    checkMessagesVerifier.verify(sourceCode.getIssues()).noMore();
  }

  @Test
  public void regex2() {
    HeaderCheck check = new HeaderCheck();
    check.headerFormat = "<!-- copyright \\d{4}\\n  mycompany -->";
    check.isRegularExpression = true;

    WebSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/HeaderCheck/Regex2.html"), check);

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(null).withMessage("Add or update the header of this file.");
  }

  @Test
  public void should_fail_with_bad_regular_expression() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("[" + HeaderCheck.class.getSimpleName() + "] Unable to compile the regular expression: *");

    HeaderCheck check = new HeaderCheck();
    check.headerFormat = "*";
    check.isRegularExpression = true;
    check.init();
  }

}
