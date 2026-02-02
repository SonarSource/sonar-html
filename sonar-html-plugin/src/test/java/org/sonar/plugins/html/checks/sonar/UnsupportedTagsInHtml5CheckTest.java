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


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

import java.io.File;

class UnsupportedTagsInHtml5CheckTest {

  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  void test() {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/UnsupportedTagsInHtml5Check.html"), new UnsupportedTagsInHtml5Check());

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLocation(1, 0, 1, 9).withMessage("Remove this deprecated \"acronym\" element.")
        .next().atLine(2).withMessage("Remove this deprecated \"applet\" element.")
        .next().atLine(3)
        .next().atLine(4)
        .next().atLine(5)
        .next().atLine(6)
        .next().atLine(7)
        .next().atLine(8)
        .next().atLine(9)
        .next().atLine(10)
        .next().atLine(11)
        .next().atLine(12)
        .next().atLine(13)
        .next().atLine(15).withMessage("Remove this deprecated \"sTrIkE\" element.");
  }

  @Test
  void vueComponentsShouldNotBeFlaggedAsDeprecatedTags() {
    // BLink is a Vue Bootstrap component (https://bootstrap-vue.org/docs/components/link)
    // It uses PascalCase naming and should NOT be confused with the deprecated <blink> HTML tag
    HtmlSourceCode sourceCode = TestHelper.scan(
        new File("src/test/resources/checks/UnsupportedTagsInHtml5Check/VueComponents.vue"),
        new UnsupportedTagsInHtml5Check());

    checkMessagesVerifier.verify(sourceCode.getIssues())
        // Only the actual lowercase <blink> tag should be flagged
        .next().atLine(7).withMessage("Remove this deprecated \"blink\" element.")
        .noMore();
  }

}
