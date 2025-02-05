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
package org.sonar.plugins.html.checks.scripting;

import java.io.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

public class NestedJavaScriptCheckTest {

  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  public void no_violations_should_be_reported_for_correct_script_tags() {
    NestedJavaScriptCheck check = new NestedJavaScriptCheck();
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/NestedJavaScriptCheck/CorrectScriptTags.html"), check);
    checkMessagesVerifier.verify(sourceCode.getIssues()).noMore();
  }

  @Test
  public void no_violations_should_be_reported_for_correct_script_tags_in_vue_templates() {
    NestedJavaScriptCheck check = new NestedJavaScriptCheck();
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/NestedJavaScriptCheck/CorrectScriptTags.vue"), check);
    checkMessagesVerifier.verify(sourceCode.getIssues()).noMore();
  }

  @Test
  public void dangling_script_end_tag_should_result_in_a_violation() {
    NestedJavaScriptCheck check = new NestedJavaScriptCheck();
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/NestedJavaScriptCheck/DanglingScriptEndTag.html"), check);
    checkMessagesVerifier.verify(sourceCode.getIssues()).next().atLine(4).noMore();
  }
  
  @Test
  public void nested_script_node_should_result_in_a_violation() {
    NestedJavaScriptCheck check = new NestedJavaScriptCheck();
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/NestedJavaScriptCheck/NestedScriptNodes.html"), check);
    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(8)
      .next().atLine(16)
      .next().atLine(25)
      .noMore();
  }

}
