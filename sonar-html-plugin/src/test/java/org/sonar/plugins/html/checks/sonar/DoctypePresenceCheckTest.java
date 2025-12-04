/*
 * SonarQube HTML
 * Copyright (C) 2010-2025 SonarSource Sàrl
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
package org.sonar.plugins.html.checks.sonar;


import java.io.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

public class DoctypePresenceCheckTest {

  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  public void doctype_before_html() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/DoctypePresenceCheck/DoctypeBeforeHtml.html"), new DoctypePresenceCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues());
  }

  @Test
  public void full_doctype_before_html() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/DoctypePresenceCheck/FullDoctypeBeforeHtml.html"), new DoctypePresenceCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues());
  }

  @Test
  public void no_doctype_before_foo() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/DoctypePresenceCheck/NoDoctypeBeforeFoo.html"), new DoctypePresenceCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues());
  }

  @Test
  public void no_doctype_before_html() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/DoctypePresenceCheck/NoDoctypeBeforeHtml.html"), new DoctypePresenceCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLocation(3, 0, 3, 6).withMessage("Insert a <!DOCTYPE> declaration to before this <hTmL> tag.");
  }

  @Test
  public void multiple_html_tags() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/DoctypePresenceCheck/MultipleHtmlTags.html"), new DoctypePresenceCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(1);
  }

  @Test
  public void doctype_after_html() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/DoctypePresenceCheck/DoctypeAfterHtml.html"), new DoctypePresenceCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(1);
  }

}
