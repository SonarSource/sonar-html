/*
 * SonarQube HTML
 * Copyright (C) 2010-2025 SonarSource SA
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

public class ElementWithGivenIdPresentCheckTest {

  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  public void test() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/ElementWithGivenIdPresentCheck/Ok.html"), new ElementWithGivenIdPresentCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues());
  }

  @Test
  public void test_angular() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/ElementWithGivenIdPresentCheck/Angular_Ok.html"), new ElementWithGivenIdPresentCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues());
  }

  @Test
  public void custom_ko() throws Exception {
    ElementWithGivenIdPresentCheck check = new ElementWithGivenIdPresentCheck();
    check.id = "gotit";

    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/ElementWithGivenIdPresentCheck/Ko.html"), check);

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().withMessage("The ID \"gotit\" is missing from this page and should be added.");
  }

  @Test
  public void custom_ok() throws Exception {
    ElementWithGivenIdPresentCheck check = new ElementWithGivenIdPresentCheck();
    check.id = "gotit";

    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/ElementWithGivenIdPresentCheck/Ok.html"), check);

    checkMessagesVerifier.verify(sourceCode.getIssues());
  }

}
