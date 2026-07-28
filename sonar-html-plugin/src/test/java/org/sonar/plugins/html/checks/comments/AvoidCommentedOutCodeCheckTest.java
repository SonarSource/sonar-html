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
package org.sonar.plugins.html.checks.comments;

import java.io.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

class AvoidCommentedOutCodeCheckTest {

  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  void detected() {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/AvoidCommentedOutCodeCheck.html"), new AvoidCommentedOutCodeCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(1).withMessage("Remove this commented out code.")
      .next().atLine(3)
      .next().atLine(5)
      .next().atLine(7)
      .next().atLine(12)
      .next().atLine(14)
      .noMore();
  }

  @Test
  void inline_tag_mentions_in_prose_are_compliant() {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/AvoidCommentedOutCodeCheckInlineMentions.html"),
      new AvoidCommentedOutCodeCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues()).noMore();
  }

  @Test
  void abrupt_html_comments_do_not_crash() {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/AvoidCommentedOutCodeCheckAbruptComments.html"),
      new AvoidCommentedOutCodeCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues()).noMore();
  }

  @Test
  void matching_pair_is_detected_despite_an_unmatched_inner_end_tag() {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/AvoidCommentedOutCodeCheckMismatchedTags.html"),
      new AvoidCommentedOutCodeCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(1).withMessage("Remove this commented out code.")
      .next().atLine(3)
      .noMore();
  }

  @Test
  void standalone_jsp_scriptlets_are_detected() {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/AvoidCommentedOutCodeCheckScriptlets.html"),
      new AvoidCommentedOutCodeCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(1).withMessage("Remove this commented out code.")
      .next().atLine(7)
      .noMore();
  }

}
