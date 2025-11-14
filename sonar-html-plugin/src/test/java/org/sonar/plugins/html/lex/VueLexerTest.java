/*
 * SonarQube HTML
 * Copyright (C) 2010-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource Sàrl.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package org.sonar.plugins.html.lex;

import java.io.StringReader;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.sonar.plugins.html.node.Node;

import static org.assertj.core.api.Assertions.assertThat;

class VueLexerTest {

  static Stream<Arguments> provideFileAndLines() {
    return Stream.of(
            Arguments.of("", 0),
            Arguments.of("<template/>", 0),
            Arguments.of("<!-- <template><foo/><bar/></template> -->", 0),
            Arguments.of("<template></template>", 0),
            Arguments.of("<template>" +
                    "<!-- some HTML code here -->" +
                    "<p>Hello, World!</p>"         +
                    "<div>"                        +
                    "Hello, again!"              +
                    "<ul>"                       +
                    "<li>foo</li>"             +
                    "<li>bar</li>"             +
                    "</ul>"                      +
                    "</div>"                       +
                    "</template>", 15),
            Arguments.of("<template><foo/><bar/><baz/></template><template><qux/></template>", 3),
            Arguments.of("<template><template><template><template></template></template></template></template>", 6),
            Arguments.of("<template><foo/><bar/>", 2),
            Arguments.of("<foo/><bar/></template>", 0)
    );
  }

  @ParameterizedTest
  @MethodSource("provideFileAndLines")
  void testMissingTemplate(String fragment, int expectedIssueCount) {
    StringReader reader = new StringReader(fragment);
    VueLexer lexer = new VueLexer();
    List<Node> nodeList = lexer.parse(reader);
    assertThat(nodeList).hasSize(expectedIssueCount);
  }
}
