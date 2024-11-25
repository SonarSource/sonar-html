/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (C) 2010-2024 SonarSource SA and Matthijs Galesloot
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package org.sonar.plugins.html.checks.comments;

import java.io.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

public class AvoidHtmlCommentCheckTest {

  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  public void should_detect_on_jsp_documents() {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/AvoidHtmlCommentCheck/document.jsp"), new AvoidHtmlCommentCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(2).withMessage("Make sure that the HTML comment does not contain sensitive information.")
        .next().atLine(4);
  }

  @Test
  public void should_detect_on_php_documents() {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/AvoidHtmlCommentCheck/document.php"), new AvoidHtmlCommentCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(6).withMessage("Make sure that the HTML comment does not contain sensitive information.");
  }

  @Test
  public void should_detect_on_erb_documents() {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/AvoidHtmlCommentCheck/document.html.erb"), new AvoidHtmlCommentCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(6).withMessage("Make sure that the HTML comment does not contain sensitive information.");
  }

  @Test
  public void should_not_detect_on_html_documents() {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/AvoidHtmlCommentCheck/document.html"), new AvoidHtmlCommentCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues());
  }

  @Test
  public void should_not_detect_on_html5_documents() {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/AvoidHtmlCommentCheck/documenthtml5.html"), new AvoidHtmlCommentCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues());
  }

  @Test
  public void should_not_detect_on_xml_documents() {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/AvoidHtmlCommentCheck/document.xml"), new AvoidHtmlCommentCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues());
  }

  @Test
  public void should_not_detect_on_xhtml_documents() {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/AvoidHtmlCommentCheck/document.xhtml"), new AvoidHtmlCommentCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues());
  }

}
