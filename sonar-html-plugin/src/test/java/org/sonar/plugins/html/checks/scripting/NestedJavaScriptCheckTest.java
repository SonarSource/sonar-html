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
package org.sonar.plugins.html.checks.scripting;

import java.io.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

class NestedJavaScriptCheckTest {

  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @ParameterizedTest
  @ValueSource(strings = {
    "CorrectScriptTags.html",
    "CorrectScriptTags.vue",
    "SingleQuotedAttributeWithLessThan.vue",
  })
  void no_violations_on_well_formed_script_tags(String fixture) {
    NestedJavaScriptCheck check = new NestedJavaScriptCheck();
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/NestedJavaScriptCheck/" + fixture), check);
    checkMessagesVerifier.verify(sourceCode.getIssues()).noMore();
  }

  @Test
  void dangling_script_end_tag_should_result_in_a_violation() {
    NestedJavaScriptCheck check = new NestedJavaScriptCheck();
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/NestedJavaScriptCheck/DanglingScriptEndTag.html"), check);
    checkMessagesVerifier.verify(sourceCode.getIssues()).next().atLine(4).noMore();
  }
  
  @Test
  void nested_script_node_should_result_in_a_violation() {
    NestedJavaScriptCheck check = new NestedJavaScriptCheck();
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/NestedJavaScriptCheck/NestedScriptNodes.html"), check);
    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(8)
      .next().atLine(16)
      .next().atLine(25)
      .noMore();
  }

}
