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

public class NonConsecutiveHeadingCheckTest {

  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  public void no_heading_tags() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/NonConsecutiveHeadingCheck/NoHeadingTags.html"), new NonConsecutiveHeadingCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues());
  }

  @Test
  public void only_h1_tags() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/NonConsecutiveHeadingCheck/OnlyH1Tags.html"), new NonConsecutiveHeadingCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues());
  }

  @Test
  public void only_h2_tags() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/NonConsecutiveHeadingCheck/OnlyH2Tags.html"), new NonConsecutiveHeadingCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(1).withMessage("Do not skip level H1.");
  }

  @Test
  public void h2_with_h1() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/NonConsecutiveHeadingCheck/H2WithH1.html"), new NonConsecutiveHeadingCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues());
  }

  @Test
  public void h5_with_h4() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/NonConsecutiveHeadingCheck/H5WithH4.html"), new NonConsecutiveHeadingCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(1).withMessage("Do not skip level H3.");
  }

}
