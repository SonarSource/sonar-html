/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (c) 2010-2024 SonarSource SA and Matthijs Galesloot
 * sonarqube@googlegroups.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
