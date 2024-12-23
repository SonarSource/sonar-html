/*
 * SonarQube HTML
 * Copyright (C) 2010-2024 SonarSource SA
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
package org.sonar.plugins.html.lex;

import java.io.StringReader;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.sonar.plugins.html.node.Node;

import static org.assertj.core.api.Assertions.assertThat;

public class VueLexerTest {

  @Test
  public void testMissingTemplate() {
    String fragment = "";

    StringReader reader = new StringReader(fragment);
    VueLexer lexer = new VueLexer();
    List<Node> nodeList = lexer.parse(reader);

    assertThat(nodeList).isEmpty();
  }

  @Test
  public void testEmptyTemplate() {
    String fragment = "<template/>";

    StringReader reader = new StringReader(fragment);
    VueLexer lexer = new VueLexer();
    List<Node> nodeList = lexer.parse(reader);

    assertThat(nodeList).isEmpty();
  }

  @Test
  public void commentedTemplate() {
    String fragment = "<!-- <template><foo/><bar/></template> -->";

    StringReader reader = new StringReader(fragment);
    VueLexer lexer = new VueLexer();
    List<Node> nodeList = lexer.parse(reader);

    assertThat(nodeList).isEmpty();
  }

  @Test
  public void testVoidTemplate() {
    String fragment = "<template></template>";

    StringReader reader = new StringReader(fragment);
    VueLexer lexer = new VueLexer();
    List<Node> nodeList = lexer.parse(reader);

    assertThat(nodeList).isEmpty();
  }

  @Test
  public void testFilledTemplate() {
    String fragment =
      "<template>" +
        "<!-- some HTML code here -->" +
        "<p>Hello, World!</p>"         +
        "<div>"                        +
          "Hello, again!"              +
          "<ul>"                       +
            "<li>foo</li>"             +
            "<li>bar</li>"             +
          "</ul>"                      +
        "</div>"                       +
      "</template>";

    StringReader reader = new StringReader(fragment);
    VueLexer lexer = new VueLexer();
    List<Node> nodeList = lexer.parse(reader);

    assertThat(nodeList).hasSize(15);
  }

  @Test
  public void testMultipleTemplates() {
    String fragment = "<template><foo/><bar/><baz/></template><template><qux/></template>";

    StringReader reader = new StringReader(fragment);
    VueLexer lexer = new VueLexer();
    List<Node> nodeList = lexer.parse(reader);

    assertThat(nodeList).hasSize(3);
  }

  @Test
  public void testNestedTemplates() {
    String fragment = "<template><template><template><template></template></template></template></template>";

    StringReader reader = new StringReader(fragment);
    VueLexer lexer = new VueLexer();
    List<Node> nodeList = lexer.parse(reader);

    assertThat(nodeList).hasSize(6);
  }

  @Test
  public void testMalformedTemplate1() {
    String fragment = "<template><foo/><bar/>";

    StringReader reader = new StringReader(fragment);
    VueLexer lexer = new VueLexer();
    List<Node> nodeList = lexer.parse(reader);

    assertThat(nodeList).hasSize(2);
  }

  @Test
  public void testMalformedTemplate2() {
    String fragment = "<foo/><bar/></template>";

    StringReader reader = new StringReader(fragment);
    VueLexer lexer = new VueLexer();
    List<Node> nodeList = lexer.parse(reader);

    assertThat(nodeList).isEmpty();
  }
}
