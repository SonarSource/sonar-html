/*
 * SonarQube HTML
 * Copyright (C) SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * You can redistribute and/or modify this program under the terms of
 * the Sonar Source-Available License Version 1, as published by SonarSource Sàrl.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package org.sonar.plugins.html.checks.security;

import java.io.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

class ContentSecurityPolicyCheckTest {

  private static final String WILDCARD = "Make sure allowing wildcards in this Content Security Policy directive is safe here.";
  private static final String UNSAFE_INLINE = "Make sure allowing 'unsafe-inline' in this Content Security Policy directive is safe here.";
  private static final String UNSAFE_HASHES = "Make sure allowing 'unsafe-hashes' in this Content Security Policy directive is safe here.";
  private static final String UNSAFE_EVAL = "Make sure allowing 'unsafe-eval' in this Content Security Policy directive is safe here.";

  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  void html() {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/ContentSecurityPolicyCheck/contentSecurityPolicy.html"),
      new ContentSecurityPolicyCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(32).withMessage(WILDCARD)
      .next().atLine(35).withMessage(UNSAFE_INLINE)
      .next().atLine(38).withMessage(UNSAFE_HASHES)
      .next().atLine(41).withMessage(UNSAFE_EVAL)
      .next().atLine(44).withMessage(UNSAFE_INLINE)
      .next().atLine(44).withMessage(WILDCARD)
      .next().atLine(47).withMessage(UNSAFE_EVAL)
      .next().atLine(47).withMessage(UNSAFE_HASHES)
      .next().atLine(47).withMessage(UNSAFE_INLINE)
      .next().atLine(47).withMessage(WILDCARD)
      .next().atLine(50).withMessage(UNSAFE_INLINE)
      .noMore();
  }

  @Test
  void reportOnlyIsNotFlagged() {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/ContentSecurityPolicyCheck/contentSecurityPolicyReportOnly.html"),
      new ContentSecurityPolicyCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .noMore();
  }

  @Test
  void razor() {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/ContentSecurityPolicyCheck/contentSecurityPolicy.cshtml"),
      new ContentSecurityPolicyCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(8).withMessage(UNSAFE_INLINE)
      .next().atLine(11).withMessage(UNSAFE_INLINE)
      .next().atLine(14).withMessage(UNSAFE_EVAL)
      .next().atLine(17).withMessage(UNSAFE_INLINE)
      .noMore();
  }

}
