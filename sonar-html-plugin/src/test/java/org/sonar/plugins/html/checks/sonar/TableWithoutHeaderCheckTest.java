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
package org.sonar.plugins.html.checks.sonar;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

import java.io.File;

class TableWithoutHeaderCheckTest {

  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  void detected() {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/TableWithoutHeaderCheck.html"), new TableWithoutHeaderCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLocation(1, 0, 1, 7).withMessage("Add \"<th>\" headers to this \"<table>\".")
        .next().atLine(27)
        .next().atLine(30)
        .next().atLine(61)
        .next().atLine(66)
        .next().atLine(73)
        .next().atLine(78);

  }

  @Test
  void razor_layout_fragment_rendering_is_compliant() {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/TableWithoutHeaderCheck/razor.cshtml"),
      new TableWithoutHeaderCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(39).withMessage("Add \"<th>\" headers to this \"<table>\".");
  }

  @Test
  void razor_vbhtml_layout_fragment_rendering_is_compliant() {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/TableWithoutHeaderCheck/razor.vbhtml"),
      new TableWithoutHeaderCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(7).withMessage("Add \"<th>\" headers to this \"<table>\".");
  }

  @Test
  void razor_like_text_in_plain_html_still_raises() {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/TableWithoutHeaderCheck/razor-tokens-in-plain-html.html"),
      new TableWithoutHeaderCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(2).withMessage("Add \"<th>\" headers to this \"<table>\".")
      .next().atLine(6).withMessage("Add \"<th>\" headers to this \"<table>\".");
  }

  @Test
  void razor_partial_in_td_still_raises() {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/TableWithoutHeaderCheck/razor-partial-in-td.cshtml"),
      new TableWithoutHeaderCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(2).withMessage("Add \"<th>\" headers to this \"<table>\".");
  }

  @Test
  void razor_nested_tables_only_suppresses_the_nearest() {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/TableWithoutHeaderCheck/razor-nested-tables.cshtml"),
      new TableWithoutHeaderCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(2).withMessage("Add \"<th>\" headers to this \"<table>\".");
  }

  @Test
  void razor_code_block_fragment_rendering_is_compliant() {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/TableWithoutHeaderCheck/razor-code-block.cshtml"),
      new TableWithoutHeaderCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues());
  }

  @Test
  void razor_partial_tag_helper_is_compliant() {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/TableWithoutHeaderCheck/razor-partial-tag-helper.cshtml"),
      new TableWithoutHeaderCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues());
  }

  @Test
  void razor_partial_tag_helper_in_td_still_raises() {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/TableWithoutHeaderCheck/razor-partial-tag-helper-in-td.cshtml"),
      new TableWithoutHeaderCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(2).withMessage("Add \"<th>\" headers to this \"<table>\".");
  }

  @Test
  void razor_explicit_expression_fragment_rendering_is_compliant() {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/TableWithoutHeaderCheck/razor-explicit-expression.cshtml"),
      new TableWithoutHeaderCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues());
  }

  @Test
  void razor_view_component_fragment_rendering_is_compliant() {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/TableWithoutHeaderCheck/razor-view-component.cshtml"),
      new TableWithoutHeaderCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues());
  }

  @Test
  void razor_server_side_comment_does_not_suppress() {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/TableWithoutHeaderCheck/razor-commented-render.cshtml"),
      new TableWithoutHeaderCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(2).withMessage("Add \"<th>\" headers to this \"<table>\".");
  }

  @Test
  void razor_escaped_at_does_not_suppress() {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/TableWithoutHeaderCheck/razor-escaped-at.cshtml"),
      new TableWithoutHeaderCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(2).withMessage("Add \"<th>\" headers to this \"<table>\".");
  }

  @Test
  void razor_tfoot_fragment_rendering_is_compliant() {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/TableWithoutHeaderCheck/razor-tfoot-fragment.cshtml"),
      new TableWithoutHeaderCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues());
  }

  @Test
  void razor_bare_vc_prefix_does_not_suppress() {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/TableWithoutHeaderCheck/razor-bare-vc-prefix.cshtml"),
      new TableWithoutHeaderCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(2).withMessage("Add \"<th>\" headers to this \"<table>\".");
  }
}
