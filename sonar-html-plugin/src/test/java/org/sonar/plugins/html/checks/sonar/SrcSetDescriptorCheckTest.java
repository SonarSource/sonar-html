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

import java.io.File;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

class SrcSetDescriptorCheckTest {
  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  void detected() {
    SrcSetDescriptorCheck check = new SrcSetDescriptorCheck();

    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/SrcSetDescriptorCheck/invalid-cases.html"),
      check
    );

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(2).withMessage("Element \"img\" has no valid and explicit descriptor.")
      .next().atLine(3).withMessage("Element \"img\" has no valid and explicit descriptor.")
      .next().atLine(4).withMessage("Element \"img\" has no valid and explicit descriptor.")
      .next().atLine(6).withMessage("Element \"source\" has no valid and explicit descriptor.")
      .next().atLine(9).withMessage("Element \"source\" has no valid and explicit descriptor.")
      .next().atLine(10).withMessage("Element \"source\" has no valid and explicit descriptor.")
      .next().atLine(14).withMessage("Element \"img\" has no valid and explicit descriptor.")
      .next().atLine(15).withMessage("Element \"img\" has no valid and explicit descriptor.")
      .next().atLine(19).withMessage("Element \"source\" has no valid and explicit descriptor.")
      .noMore();
  }

  @Test
  void shouldNotRaiseOnValidExamples() {
    SrcSetDescriptorCheck check = new SrcSetDescriptorCheck();

    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/SrcSetDescriptorCheck/valid-cases.html"),
      check
    );

    checkMessagesVerifier.verify(sourceCode.getIssues()).noMore();
  }
  
}
