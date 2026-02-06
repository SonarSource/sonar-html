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
package org.sonar.plugins.html.checks.sonar;

import java.io.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

class IndistinguishableSimilarElementsCheckTest {
  
  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  void singleNavAside() {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/IndistinguishableSimilarElementsCheck/SingleNavAside.html"), new IndistinguishableSimilarElementsCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues());
  }

  @Test
  void vueConditional() {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/IndistinguishableSimilarElementsCheck/VueConditional.vue"),
      new IndistinguishableSimilarElementsCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(12).withMessage("Add an \"aria-label\" or \"aria-labbelledby\" attribute to this element.")
        .next().atLine(13)
        .noMore();
  }

  @Test
  void multipleNavAside() {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/IndistinguishableSimilarElementsCheck/MultipleNavAside.html"), new IndistinguishableSimilarElementsCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLocation(1, 0, 1, 5).withMessage("Add an \"aria-label\" or \"aria-labbelledby\" attribute to this element.")
        .next().atLine(4)
        .next().atLine(13)
        .next().atLine(16)
        .next().atLine(25)
        .next().atLine(28)
        .next().atLine(31)
        .next().atLine(55);
  }
}
