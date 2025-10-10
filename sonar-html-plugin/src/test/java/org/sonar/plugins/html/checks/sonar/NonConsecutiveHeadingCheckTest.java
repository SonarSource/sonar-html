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

import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

class NonConsecutiveHeadingCheckTest {

  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  static Stream<Arguments> provideFileAndLinesAndMessages() {
    return Stream.of(
            Arguments.of("src/test/resources/checks/NonConsecutiveHeadingCheck/NoHeadingTags.html", new int[]{}, new String[]{}),
            Arguments.of("src/test/resources/checks/NonConsecutiveHeadingCheck/OnlyH1Tags.html", new int[]{}, new String[]{}),
            Arguments.of("src/test/resources/checks/NonConsecutiveHeadingCheck/OnlyH2Tags.html", new int[]{1}, new String[]{"Do not skip level H1."}),
            Arguments.of("src/test/resources/checks/NonConsecutiveHeadingCheck/H2WithH1.html", new int[]{}, new String[]{}),
            Arguments.of("src/test/resources/checks/NonConsecutiveHeadingCheck/H5WithH4.html", new int[]{1}, new String[]{"Do not skip level H3."}),
            Arguments.of("src/test/resources/checks/AvoidHtmlCommentCheck/document.xml", new int[]{}, new String[]{}),
            Arguments.of("src/test/resources/checks/AvoidHtmlCommentCheck/document.xhtml", new int[]{}, new String[]{})
    );
  }

  @ParameterizedTest
  @MethodSource("provideFileAndLinesAndMessages")
  void test(String file, int[] lines, String[] messages) {
    HtmlSourceCode sourceCode = TestHelper.scan(new File(file), new NonConsecutiveHeadingCheck());

    var checker = checkMessagesVerifier.verify(sourceCode.getIssues());
    for (var idx = 0; idx < lines.length; idx++) {
      checker.next().atLine(lines[idx]).withMessage(messages[idx]);
    }
    checker.noMore();
  }
}
