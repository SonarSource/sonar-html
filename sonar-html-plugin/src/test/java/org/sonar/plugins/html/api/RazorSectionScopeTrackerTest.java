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
package org.sonar.plugins.html.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.sonar.plugins.html.lex.PageLexer;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

class RazorSectionScopeTrackerTest {

  @Test
  void tracks_tags_inside_razor_sections() {
    ParsedDocument document = parse("file.cshtml", """
      @section LegendSection {
        <legend>In section</legend>
      }

      <legend>At root</legend>

      @section TermsSection {
        <li>Also in section</li>
      }
      """);

    RazorSectionScopeTracker tracker = RazorSectionScopeTracker.create(document.nodes(), document.sourceCode());

    assertThat(tracker.contains(findTag(document.nodes(), "legend", 2))).isTrue();
    assertThat(tracker.contains(findTag(document.nodes(), "legend", 5))).isFalse();
    assertThat(tracker.contains(findTag(document.nodes(), "li", 8))).isTrue();
  }

  @Test
  void ignores_literal_braces_inside_html_body_when_tracking_sections() {
    ParsedDocument document = parse("file.cshtml", """
      @section LegendSection {
        <p>literal close: }</p>
        <legend>Still in section</legend>
      }

      <legend>Outside section</legend>
      """);

    RazorSectionScopeTracker tracker = RazorSectionScopeTracker.create(document.nodes(), document.sourceCode());

    assertThat(tracker.contains(findTag(document.nodes(), "legend", 3))).isTrue();
    assertThat(tracker.contains(findTag(document.nodes(), "legend", 6))).isFalse();
  }

  @Test
  void returns_empty_tracker_for_non_razor_files() {
    ParsedDocument document = parse("file.html", """
      @section LegendSection {
        <legend>Not Razor</legend>
      }
      """);

    RazorSectionScopeTracker tracker = RazorSectionScopeTracker.create(document.nodes(), document.sourceCode());

    assertThat(tracker.contains(findTag(document.nodes(), "legend", 2))).isFalse();
  }

  private static ParsedDocument parse(String filename, String content) {
    List<Node> nodes = new PageLexer().parse(new StringReader(content));
    HtmlSourceCode sourceCode = new HtmlSourceCode(
      new TestInputFileBuilder("key", filename)
        .setLanguage(HtmlConstants.LANGUAGE_KEY)
        .setType(InputFile.Type.MAIN)
        .setCharset(StandardCharsets.UTF_8)
        .build()
    );
    return new ParsedDocument(nodes, sourceCode);
  }

  private static TagNode findTag(List<Node> nodes, String tagName, int startLine) {
    return nodes.stream()
      .filter(TagNode.class::isInstance)
      .map(TagNode.class::cast)
      .filter(tag -> !tag.isEndElement())
      .filter(tag -> tagName.equalsIgnoreCase(tag.getNodeName()))
      .filter(tag -> tag.getStartLinePosition() == startLine)
      .findFirst()
      .orElseThrow(() -> new IllegalArgumentException("No <" + tagName + "> tag at line " + startLine));
  }

  private record ParsedDocument(List<Node> nodes, HtmlSourceCode sourceCode) {
  }
}
