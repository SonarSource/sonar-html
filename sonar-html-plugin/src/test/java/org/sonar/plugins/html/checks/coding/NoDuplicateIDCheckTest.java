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
package org.sonar.plugins.html.checks.coding;

import java.io.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

class NoDuplicateIDCheckTest {

  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  void duplicateIdsOutsideConditionals() {
    HtmlSourceCode sourceCode = TestHelper.scan(
        new File("src/test/resources/checks/NoDuplicateIDCheck.html"),
        new NoDuplicateIDCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(31).withMessage("Duplicate id \"duplicate\" found. First occurrence was on line 30.")
        .next().atLine(35).withMessage("Duplicate id \"article1\" found. First occurrence was on line 34.")
        .next().atLine(39).withMessage("Duplicate id \"Article1\" found. First occurrence was on line 38.")
        .noMore();
  }

  @Test
  void jspConditionalBlocks() {
    HtmlSourceCode sourceCode = TestHelper.scan(
        new File("src/test/resources/checks/NoDuplicateIDCheck/conditionalBlocks.jsp"),
        new NoDuplicateIDCheck());

    // IDs in mutually exclusive c:if blocks or c:choose/c:when branches should NOT be flagged
    // Only the actual duplicate outside conditionals should be flagged
    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(33).withMessage("Duplicate id \"footer\" found. First occurrence was on line 32.")
        .noMore();
  }

  @Test
  void phpConditionalBlocks() {
    HtmlSourceCode sourceCode = TestHelper.scan(
        new File("src/test/resources/checks/NoDuplicateIDCheck/conditionalBlocks.phtml"),
        new NoDuplicateIDCheck());

    // IDs in mutually exclusive PHP if/else branches should NOT be flagged,
    // and PHP directives/comments with literal braces must not leak conditional depth
    // Only the actual duplicate outside conditionals should be flagged
    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(31).withMessage("Duplicate id \"footer\" found. First occurrence was on line 30.")
        .noMore();
  }

  @Test
  void phpConditionalBlocksWithElseIf() {
    HtmlSourceCode sourceCode = TestHelper.scan(
        new File("src/test/resources/checks/NoDuplicateIDCheck/conditionalBlocksElseIf.phtml"),
        new NoDuplicateIDCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(14).withMessage("Duplicate id \"footer\" found. First occurrence was on line 13.")
        .noMore();
  }

  @Test
  void vueConditionalBlocks() {
    HtmlSourceCode sourceCode = TestHelper.scan(
        new File("src/test/resources/checks/NoDuplicateIDCheck/conditionalBlocks.vue"),
        new NoDuplicateIDCheck());

    // IDs in v-if/v-else-if/v-else blocks should NOT be flagged
    // Only the actual duplicate outside conditionals should be flagged
    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(29).withMessage("Duplicate id \"static-elem\" found. First occurrence was on line 28.")
        .noMore();
  }

  @Test
  void razorSwitchBlocks() {
    HtmlSourceCode sourceCode = TestHelper.scan(
        new File("src/test/resources/checks/NoDuplicateIDCheck/conditionalBlocksRazorSwitch.cshtml"),
        new NoDuplicateIDCheck());

    // Razor @switch uses plain case:/default: labels; ids across cases are mutually exclusive
    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(20).withMessage("Duplicate id \"footer\" found. First occurrence was on line 19.")
        .noMore();
  }

  @Test
  void angularSwitchWithBraceInCaseLabel() {
    HtmlSourceCode sourceCode = TestHelper.scan(
        new File("src/test/resources/checks/NoDuplicateIDCheck/conditionalBlocksAngularCaseBrace.cshtml"),
        new NoDuplicateIDCheck());

    // A brace inside an Angular @case label must not close the @switch early
    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(19).withMessage("Duplicate id \"footer\" found. First occurrence was on line 18.")
        .noMore();
  }

  @Test
  void angularConditionalBlocks() {
    HtmlSourceCode sourceCode = TestHelper.scan(
        new File("src/test/resources/checks/NoDuplicateIDCheck/conditionalBlocksAngular.html"),
        new NoDuplicateIDCheck());

    // IDs in @switch/@case/@default, @if/@else, or *ngIf blocks should NOT be flagged
    // Only the actual duplicate outside conditionals should be flagged
    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(35).withMessage("Duplicate id \"badge\" found. First occurrence was on line 34.")
        .noMore();
  }

  @Test
  void razorConditionalBlocks() {
    HtmlSourceCode sourceCode = TestHelper.scan(
        new File("src/test/resources/checks/NoDuplicateIDCheck/conditionalBlocks.cshtml"),
        new NoDuplicateIDCheck());

    // IDs in @if/@else blocks should NOT be flagged
    // Only the actual duplicate outside conditionals should be flagged
    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(29).withMessage("Duplicate id \"wrapper\" found. First occurrence was on line 28.")
        .noMore();
  }

  /**
   * Ignores duplicate ids across Razor if/else branches when one branch contains nested C# blocks.
   */
  @Test
  void razorConditionalBlocksWithNestedCSharpBlocks() {
    HtmlSourceCode sourceCode = TestHelper.scan(
        new File("src/test/resources/checks/NoDuplicateIDCheck/conditionalBlocksNested.cshtml"),
        new NoDuplicateIDCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(18).withMessage("Duplicate id \"wrapper\" found. First occurrence was on line 17.")
        .noMore();
  }

  @Test
  void razorConditionalBlocksWithNestedCSharpString() {
    HtmlSourceCode sourceCode = TestHelper.scan(
        new File("src/test/resources/checks/NoDuplicateIDCheck/conditionalBlocksNestedCSharpString.cshtml"),
        new NoDuplicateIDCheck());

    // A brace inside a C# string in a nested code block must not close the branch early
    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(18).withMessage("Duplicate id \"footer\" found. First occurrence was on line 17.")
        .noMore();
  }

  @Test
  void razorConditionalBlocksWithBracesInCondition() {
    HtmlSourceCode sourceCode = TestHelper.scan(
        new File("src/test/resources/checks/NoDuplicateIDCheck/conditionalBlocksConditionBraces.cshtml"),
        new NoDuplicateIDCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(16).withMessage("Duplicate id \"wrapper\" found. First occurrence was on line 15.")
        .noMore();
  }

  @Test
  void razorConditionalBlocksWithStyleSelector() {
    HtmlSourceCode sourceCode = TestHelper.scan(
        new File("src/test/resources/checks/NoDuplicateIDCheck/conditionalBlocksStyleBlock.cshtml"),
        new NoDuplicateIDCheck());

    // A CSS id selector inside a branch must not be read as a code comment; its braces stay balanced
    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(21).withMessage("Duplicate id \"wrapper\" found. First occurrence was on line 20.")
        .noMore();
  }

  @Test
  void razorConditionalBlocksWithApostropheInText() {
    HtmlSourceCode sourceCode = TestHelper.scan(
        new File("src/test/resources/checks/NoDuplicateIDCheck/conditionalBlocksTextApostrophe.cshtml"),
        new NoDuplicateIDCheck());

    // An apostrophe in branch text must not open a string that swallows the closing brace
    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(15).withMessage("Duplicate id \"dup\" found. First occurrence was on line 14.")
        .noMore();
  }

  @Test
  void razorConditionalBlocksWithRazorCommentBeforeElse() {
    HtmlSourceCode sourceCode = TestHelper.scan(
        new File("src/test/resources/checks/NoDuplicateIDCheck/conditionalBlocksRazorComment.cshtml"),
        new NoDuplicateIDCheck());

    // A Razor comment between the closing brace and else must not break the branch chain
    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(19).withMessage("Duplicate id \"wrapper\" found. First occurrence was on line 18.")
        .noMore();
  }

  @Test
  void razorConditionalBlocksWithHtmlCommentBeforeElse() {
    HtmlSourceCode sourceCode = TestHelper.scan(
        new File("src/test/resources/checks/NoDuplicateIDCheck/conditionalBlocksHtmlComment.cshtml"),
        new NoDuplicateIDCheck());

    // An HTML comment splits the text node; the else opening the next fragment must still continue the chain
    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(18).withMessage("Duplicate id \"wrapper\" found. First occurrence was on line 17.")
        .noMore();
  }

  @Test
  void razorConditionalBlocksWithElseIf() {
    HtmlSourceCode sourceCode = TestHelper.scan(
        new File("src/test/resources/checks/NoDuplicateIDCheck/conditionalBlocksRazorElseIf.cshtml"),
        new NoDuplicateIDCheck());

    // IDs across @if/else if/else branches are mutually exclusive, even when the else if condition
    // contains braces; only the outside duplicate is flagged
    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(20).withMessage("Duplicate id \"footer\" found. First occurrence was on line 19.")
        .noMore();
  }

  @Test
  void razorConditionalBlocksWithCodeBlockPreamble() {
    HtmlSourceCode sourceCode = TestHelper.scan(
        new File("src/test/resources/checks/NoDuplicateIDCheck/conditionalBlocksRazorPreamble.cshtml"),
        new NoDuplicateIDCheck());

    // A top-level @{ ... } code block containing a C# if must not consume the conditional depth of the
    // following @if/else if chain; ids across those branches stay mutually exclusive
    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(27).withMessage("Duplicate id \"footer\" found. First occurrence was on line 26.")
        .noMore();
  }

  @Test
  void plainCSharpConditionalBlocksInsideRazorCode() {
    HtmlSourceCode sourceCode = TestHelper.scan(
        new File("src/test/resources/checks/NoDuplicateIDCheck/conditionalBlocksPlainCSharp.cshtml"),
        new NoDuplicateIDCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(42).withMessage("Duplicate id \"footer\" found. First occurrence was on line 41.")
        .noMore();
  }

  @Test
  void htmlLikeTagsInsideRazorAndCSharpComments() {
    HtmlSourceCode sourceCode = TestHelper.scan(
        new File("src/test/resources/checks/NoDuplicateIDCheck/commentsInsideRazorCode.cshtml"),
        new NoDuplicateIDCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(34).withMessage("Duplicate id \"footer\" found. First occurrence was on line 33.")
        .noMore();
  }

  @Test
  void razorTokensInPlainHtml() {
    HtmlSourceCode sourceCode = TestHelper.scan(
        new File("src/test/resources/checks/NoDuplicateIDCheck/razorTokensInPlainHtml.html"),
        new NoDuplicateIDCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(4).withMessage("Duplicate id \"code-block\" found. First occurrence was on line 3.")
        .next().atLine(8).withMessage("Duplicate id \"comment\" found. First occurrence was on line 7.")
        .noMore();
  }

  @Test
  void razorConditionalBlocksWithScriptTemplateLiteral() {
    HtmlSourceCode sourceCode = TestHelper.scan(
        new File("src/test/resources/checks/NoDuplicateIDCheck/conditionalBlocksScriptTemplateLiteral.cshtml"),
        new NoDuplicateIDCheck());

    // A brace inside a backtick template literal in a script body must not close the conditional
    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(19).withMessage("Duplicate id \"footer\" found. First occurrence was on line 18.")
        .noMore();
  }

  @Test
  void razorConditionalBlocksWithScriptString() {
    HtmlSourceCode sourceCode = TestHelper.scan(
        new File("src/test/resources/checks/NoDuplicateIDCheck/conditionalBlocksScriptString.cshtml"),
        new NoDuplicateIDCheck());

    // A brace inside a quoted string in a script body must not close the conditional
    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(19).withMessage("Duplicate id \"footer\" found. First occurrence was on line 18.")
        .noMore();
  }

  @Test
  void razorConditionalBlocksWithScriptLineComment() {
    HtmlSourceCode sourceCode = TestHelper.scan(
        new File("src/test/resources/checks/NoDuplicateIDCheck/conditionalBlocksScriptLineComment.cshtml"),
        new NoDuplicateIDCheck());

    // A brace inside a // line comment in a script body must not close the conditional
    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(19).withMessage("Duplicate id \"footer\" found. First occurrence was on line 18.")
        .noMore();
  }

  @Test
  void razorConditionalBlocksWithStyleUrl() {
    HtmlSourceCode sourceCode = TestHelper.scan(
        new File("src/test/resources/checks/NoDuplicateIDCheck/conditionalBlocksStyleUrl.cshtml"),
        new NoDuplicateIDCheck());

    // // is not a comment in CSS: the slashes in a url() must not be treated as a line comment
    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(19).withMessage("Duplicate id \"footer\" found. First occurrence was on line 18.")
        .noMore();
  }

  @Test
  void razorConditionalBlocksWithStyleString() {
    HtmlSourceCode sourceCode = TestHelper.scan(
        new File("src/test/resources/checks/NoDuplicateIDCheck/conditionalBlocksStyleString.cshtml"),
        new NoDuplicateIDCheck());

    // A brace inside a quoted string in a style body must not close the conditional
    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(19).withMessage("Duplicate id \"footer\" found. First occurrence was on line 18.")
        .noMore();
  }

  @Test
  void razorConditionalBlocksWithMalformedElseIf() {
    HtmlSourceCode sourceCode = TestHelper.scan(
        new File("src/test/resources/checks/NoDuplicateIDCheck/conditionalBlocksMalformedElseIf.cshtml"),
        new NoDuplicateIDCheck());

    // A malformed else if without parentheses must not swallow the rest of the file
    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(15).withMessage("Duplicate id \"dup\" found. First occurrence was on line 14.")
        .noMore();
  }

  @Test
  void twigConditionalBlocks() {
    HtmlSourceCode sourceCode = TestHelper.scan(
        new File("src/test/resources/checks/NoDuplicateIDCheck/conditionalBlocksTwig.html"),
        new NoDuplicateIDCheck());

    // IDs in {% if %}/{% else %}/{% elif %} blocks should NOT be flagged
    // Only the actual duplicate outside conditionals should be flagged
    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(23).withMessage("Duplicate id \"username\" found. First occurrence was on line 22.")
        .noMore();
  }

  @Test
  void twigCommentsDoNotCreateDuplicateIds() {
    HtmlSourceCode sourceCode = TestHelper.scan(
        new File("src/test/resources/checks/NoDuplicateIDCheck/twigComments.twig"),
        new NoDuplicateIDCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(3).withMessage("Duplicate id \"username\" found. First occurrence was on line 2.")
        .noMore();
  }

  @Test
  void jinjaConditionalBlocks() {
    HtmlSourceCode sourceCode = TestHelper.scan(
        new File("src/test/resources/checks/NoDuplicateIDCheck/conditionalBlocksJinja.html"),
        new NoDuplicateIDCheck());

    // IDs in {%- if -%}/{%- else -%} blocks should NOT be flagged
    // Only the actual duplicate outside conditionals should be flagged
    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(21).withMessage("Duplicate id \"header\" found. First occurrence was on line 20.")
        .noMore();
  }

  @Test
  void dynamicIdsInRazor() {
    HtmlSourceCode sourceCode = TestHelper.scan(
        new File("src/test/resources/checks/NoDuplicateIDCheck/dynamicIds.cshtml"),
        new NoDuplicateIDCheck());

    // Dynamic IDs containing Razor expressions (@variable) should NOT be flagged
    // Only static duplicate IDs should be flagged
    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(17).withMessage("Duplicate id \"static-id\" found. First occurrence was on line 16.")
        .noMore();
  }

  @Test
  void dynamicIdsWithTemplateExpressions() {
    HtmlSourceCode sourceCode = TestHelper.scan(
        new File("src/test/resources/checks/NoDuplicateIDCheck/dynamicIds.html"),
        new NoDuplicateIDCheck());

    // Dynamic IDs containing template expressions ({{...}}, ${...}) should NOT be flagged
    // Only static duplicate IDs should be flagged
    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(19).withMessage("Duplicate id \"footer\" found. First occurrence was on line 18.")
        .noMore();
  }

  @Test
  void generatedClientIdsInWebFormsNamingContainers() {
    HtmlSourceCode sourceCode = TestHelper.scan(
        new File("src/test/resources/checks/NoDuplicateIDCheck/webFormsNamingContainers.aspx"),
        new NoDuplicateIDCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues()).noMore();
  }

  @Test
  void overlappingFormViewAndDetailsViewTemplateScopes() {
    HtmlSourceCode sourceCode = TestHelper.scan(
        new File("src/test/resources/checks/NoDuplicateIDCheck/webFormsOverlappingFormScopes.aspx"),
        new NoDuplicateIDCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(7).withMessage("Duplicate id \"form-item-scope\" found. First occurrence was on line 3.")
        .next().atLine(8).withMessage("Duplicate id \"form-edit-scope\" found. First occurrence was on line 4.")
        .next().atLine(9).withMessage("Duplicate id \"form-insert-scope\" found. First occurrence was on line 5.")
        .next().atLine(18).withMessage("Duplicate id \"details-item-scope\" found. First occurrence was on line 14.")
        .next().atLine(19).withMessage("Duplicate id \"details-edit-scope\" found. First occurrence was on line 15.")
        .next().atLine(20).withMessage("Duplicate id \"details-insert-scope\" found. First occurrence was on line 16.")
        .noMore();
  }

  @Test
  void prefixedCustomControlIsNotAssumedToBeANamingContainer() {
    HtmlSourceCode sourceCode = TestHelper.scan(
        new File("src/test/resources/checks/NoDuplicateIDCheck/webFormsCustomNonContainer.aspx"),
        new NoDuplicateIDCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(10).withMessage("Duplicate id \"custom-non-container-id\" found. First occurrence was on line 5.")
        .noMore();
  }

  @Test
  void duplicateIdsWithoutDistinctWebFormsRuntimeScopes() {
    HtmlSourceCode sourceCode = TestHelper.scan(
        new File("src/test/resources/checks/NoDuplicateIDCheck/webFormsDuplicates.aspx"),
        new NoDuplicateIDCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(4).withMessage("Duplicate id \"same-template\" found. First occurrence was on line 3.")
        .next().atLine(13).withMessage("Duplicate id \"same-item-scope\" found. First occurrence was on line 10.")
        .next().atLine(25).withMessage("Duplicate id \"static-client-id\" found. First occurrence was on line 19.")
        .next().atLine(36).withMessage("Duplicate id \"literal-id\" found. First occurrence was on line 31.")
        .next().atLine(48).withMessage("Duplicate id \"inherited-static-id\" found. First occurrence was on line 42.")
        .next().atLine(54).withMessage("Duplicate id \"page-scope\" found. First occurrence was on line 53.")
        .next().atLine(59).withMessage("Duplicate id \"same-layout-scope\" found. First occurrence was on line 58.")
        .next().atLine(65).withMessage("Duplicate id \"same-form-scope\" found. First occurrence was on line 64.")
        .next().atLine(70).withMessage("Duplicate id \"same-details-scope\" found. First occurrence was on line 69.")
        .next().atLine(76).withMessage("Duplicate id \"same-data-list-scope\" found. First occurrence was on line 75.")
        .next().atLine(83).withMessage("Duplicate id \"same-data-grid-scope\" found. First occurrence was on line 82.")
        .next().atLine(89).withMessage("Duplicate id \"same-content-scope\" found. First occurrence was on line 88.")
        .next().atLine(102).withMessage("Duplicate id \"naming-container-inheritance-id\" found. First occurrence was on line 95.")
        .next().atLine(111).withMessage("Duplicate id \"unprefixed-container-id\" found. First occurrence was on line 108.")
        .next().atLine(120).withMessage("Duplicate id \"loginview-static-id\" found. First occurrence was on line 117.")
        .next().atLine(132).withMessage("Duplicate id \"wizard-static-id\" found. First occurrence was on line 129.")
        .next().atLine(141).withMessage("Duplicate id \"same-menu-template-scope\" found. First occurrence was on line 140.")
        .next().atLine(147).withMessage("Duplicate id \"same-site-map-template-scope\" found. First occurrence was on line 146.")
        .next().atLine(155).withMessage("Duplicate id \"wizard-shared-step-scope\" found. First occurrence was on line 152.")
        .next().atLine(158).withMessage("Duplicate id \"wizard-shared-step-scope\" found. First occurrence was on line 152.")
        .next().atLine(166).withMessage("Duplicate id \"same-wizard-step-scope\" found. First occurrence was on line 165.")
        .noMore();
  }

  @Test
  void staticClientIdsInheritedFromBuiltInWebFormsNamingContainers() {
    HtmlSourceCode sourceCode = TestHelper.scan(
        new File("src/test/resources/checks/NoDuplicateIDCheck/webFormsBuiltInNamingContainerInheritance.aspx"),
        new NoDuplicateIDCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(9).withMessage("Duplicate id \"create-user-wizard-static-id\" found. First occurrence was on line 6.")
        .next().atLine(22).withMessage("Duplicate id \"change-password-static-id\" found. First occurrence was on line 19.")
        .next().atLine(33).withMessage("Duplicate id \"login-static-id\" found. First occurrence was on line 30.")
        .next().atLine(44).withMessage("Duplicate id \"password-recovery-static-id\" found. First occurrence was on line 41.")
        .next().atLine(55).withMessage("Duplicate id \"menu-static-id\" found. First occurrence was on line 52.")
        .next().atLine(66).withMessage("Duplicate id \"site-map-static-id\" found. First occurrence was on line 63.")
        .noMore();
  }

  @Test
  void staticClientIdsInheritedFromWebFormsPage() {
    HtmlSourceCode sourceCode = TestHelper.scan(
        new File("src/test/resources/checks/NoDuplicateIDCheck/webFormsStaticPage.aspx"),
        new NoDuplicateIDCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(13).withMessage("Duplicate id \"page-static-id\" found. First occurrence was on line 6.")
        .noMore();
  }

  @Test
  void remappedAspPrefixIsNotAssumedToContainBuiltInControls() {
    HtmlSourceCode sourceCode = TestHelper.scan(
        new File("src/test/resources/checks/NoDuplicateIDCheck/webFormsRemappedAspPrefix.aspx"),
        new NoDuplicateIDCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(10).withMessage("Duplicate id \"remapped-asp-prefix-id\" found. First occurrence was on line 5.")
        .noMore();
  }

  @Test
  void webFormsDirectivesApplyRegardlessOfDocumentOrder() {
    HtmlSourceCode sourceCode = TestHelper.scan(
        new File("src/test/resources/checks/NoDuplicateIDCheck/webFormsDirectiveOrdering.aspx"),
        new NoDuplicateIDCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(19).withMessage("Duplicate id \"late-page-mode-id\" found. First occurrence was on line 14.")
        .noMore();
  }

  @Test
  void registeredUserControlsAreNamingContainers() {
    HtmlSourceCode sourceCode = TestHelper.scan(
        new File("src/test/resources/checks/NoDuplicateIDCheck/webFormsRegisteredUserControl.aspx"),
        new NoDuplicateIDCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(13).withMessage("Duplicate id \"same-user-control-scope\" found. First occurrence was on line 12.")
        .next().atLine(21).withMessage("Duplicate id \"same-built-in-named-user-control-scope\" found. First occurrence was on line 18.")
        .noMore();
  }

  @Test
  void staticClientIdsInheritedFromWebFormsControl() {
    HtmlSourceCode sourceCode = TestHelper.scan(
        new File("src/test/resources/checks/NoDuplicateIDCheck/webFormsStaticControl.ascx"),
        new NoDuplicateIDCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(11).withMessage("Duplicate id \"control-static-id\" found. First occurrence was on line 4.")
        .noMore();
  }
}
