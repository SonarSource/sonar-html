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
package org.sonar.plugins.html.checks.sonar;


import java.io.File;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

class DoctypePresenceCheckTest {

  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  static Stream<Arguments> provideFileAndLines() {
    return Stream.of(
            Arguments.of("src/test/resources/checks/DoctypePresenceCheck/DoctypeBeforeHtml.html", new int[]{}),
            Arguments.of("src/test/resources/checks/DoctypePresenceCheck/FullDoctypeBeforeHtml.html", new int[]{}),
            Arguments.of("src/test/resources/checks/DoctypePresenceCheck/NoDoctypeBeforeFoo.html", new int[]{}),
            Arguments.of("src/test/resources/checks/DoctypePresenceCheck/MultipleHtmlTags.html", new int[]{1}),
            Arguments.of("src/test/resources/checks/DoctypePresenceCheck/DoctypeAfterHtml.html", new int[]{1})
    );
  }

  @Test
  void no_doctype_before_html() {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/DoctypePresenceCheck/NoDoctypeBeforeHtml.html"), new DoctypePresenceCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLocation(3, 0, 3, 6).withMessage("Insert a <!DOCTYPE> declaration to before this <hTmL> tag.");
  }

  @ParameterizedTest
  @MethodSource("provideFileAndLines")
  void test(String file, int[] lines) {
    HtmlSourceCode sourceCode = TestHelper.scan(new File(file), new DoctypePresenceCheck());

    var checker = checkMessagesVerifier.verify(sourceCode.getIssues());
    for (var line : lines) {
      checker.next().atLine(line).withMessage("Insert a <!DOCTYPE> declaration to before this <html> tag.");
    }
    checker.noMore();
  }
}
