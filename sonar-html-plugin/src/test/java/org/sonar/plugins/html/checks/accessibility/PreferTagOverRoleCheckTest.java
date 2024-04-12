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
package org.sonar.plugins.html.checks.accessibility;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

class PreferTagOverRoleCheckTest {
  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  void html() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/PreferTagOverRoleCheck.html"),
      new PreferTagOverRoleCheck());
    var issues = sourceCode.getIssues();
    assertThat(issues).hasSize(8);
    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(1).withMessage("Use <input> instead of the checkbox role to ensure accessibility across all devices.")
      .next().atLine(2).withMessage("Use <button> or <input> instead of the button role to ensure accessibility across all devices.")
      .next().atLine(3).withMessage("Use <h1> or <h2> or <h3> or <h4> or <h5> or <h6> instead of the heading role to ensure accessibility across all devices.")
      .next().atLine(4).withMessage("Use <a> or <area> instead of the link role to ensure accessibility across all devices.")
      .next().atLine(5).withMessage("Use <tbody> or <tfoot> or <thead> instead of the rowgroup role to ensure accessibility across all devices.")
      .next().atLine(6).withMessage("Use <input> instead of the checkbox role to ensure accessibility across all devices.")
      .next().atLine(7).withMessage("Use <input> instead of the checkbox role to ensure accessibility across all devices.")
      .next().atLine(8).withMessage("Use <header> instead of the banner role to ensure accessibility across all devices.")
      .consume();
  }
}