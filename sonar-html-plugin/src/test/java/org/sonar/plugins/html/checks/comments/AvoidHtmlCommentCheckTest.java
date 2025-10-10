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
package org.sonar.plugins.html.checks.comments;

import java.io.File;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

class AvoidHtmlCommentCheckTest {

  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  static Stream<Arguments> provideFileAndLines() {
    return Stream.of(
            Arguments.of("src/test/resources/checks/AvoidHtmlCommentCheck/document.jsp", new int[]{2, 4}),
            Arguments.of("src/test/resources/checks/AvoidHtmlCommentCheck/document.php", new int[]{6}),
            Arguments.of("src/test/resources/checks/AvoidHtmlCommentCheck/document.html.erb", new int[]{6}),
            Arguments.of("src/test/resources/checks/AvoidHtmlCommentCheck/document.html", new int[]{}),
            Arguments.of("src/test/resources/checks/AvoidHtmlCommentCheck/documenthtml5.html", new int[]{}),
            Arguments.of("src/test/resources/checks/AvoidHtmlCommentCheck/document.xml", new int[]{}),
            Arguments.of("src/test/resources/checks/AvoidHtmlCommentCheck/document.xhtml", new int[]{})
    );
  }

  @ParameterizedTest
  @MethodSource("provideFileAndLines")
  void should_detect(String file, int[] lines) {
    HtmlSourceCode sourceCode = TestHelper.scan(new File(file), new AvoidHtmlCommentCheck());

    var checker = checkMessagesVerifier.verify(sourceCode.getIssues());
    for (var line : lines) {
      checker.next().atLine(line).withMessage("Make sure that the HTML comment does not contain sensitive information.");
    }
    checker.noMore();
  }
}
