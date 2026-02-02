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
}
