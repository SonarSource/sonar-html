/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (c) 2010-2023 SonarSource SA and Matthijs Galesloot
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
