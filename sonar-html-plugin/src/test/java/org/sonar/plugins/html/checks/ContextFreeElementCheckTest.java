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
package org.sonar.plugins.html.checks;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sonar.plugins.html.node.TagNode;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

import static org.assertj.core.api.Assertions.assertThat;

class ContextFreeElementCheckTest {

  static class RecordingCheck extends ContextFreeElementCheck {
    final List<String> seen = new ArrayList<>();

    @Override
    public void startElement(TagNode element) {
      StringBuilder entry = new StringBuilder(element.getNodeName());
      for (var attr : element.getAttributes()) {
        entry.append(' ').append(attr.getName()).append('=').append(attr.getValue());
      }
      seen.add(entry.toString());
    }
  }

  private RecordingCheck check;

  @BeforeEach
  void setUp() {
    check = new RecordingCheck();
  }

  private HtmlSourceCode scan(String fileName) {
    return TestHelper.scan(new File("src/test/resources/checks/ContextFreeElementCheck/" + fileName), check);
  }

  @Test
  void htmlInPhpStringReachesStartElement() {
    scan("plain-tag.php");
    assertThat(check.seen).contains("div role=toolbar");
  }

  @Test
  void dollarVarIsNormalisedToDynamic() {
    scan("dynamic-var.php");
    assertThat(check.seen).anyMatch(s -> s.contains("role=${dynamic}"));
    assertThat(check.seen).noneMatch(s -> s.contains("$role"));
  }

  @Test
  void curlyExprIsNormalisedToDynamic() {
    scan("dynamic-curly.php");
    assertThat(check.seen).anyMatch(s -> s.contains("id=${dynamic}"));
  }

  @Test
  void echoExprIsNormalisedToDynamic() {
    scan("dynamic-echo.php");
    assertThat(check.seen).anyMatch(s -> s.contains("class=${dynamic}"));
  }

  @Test
  void partialInterpolationCollapasesToDynamic() {
    // "fn:$note_id" → the attribute value contains the marker and is fully replaced
    scan("partial-interp.php");
    assertThat(check.seen).anyMatch(s -> s.contains("id=${dynamic}"));
    assertThat(check.seen).noneMatch(s -> s.contains("fn:"));
  }

  @Test
  void closingTagsAreNotDispatched() {
    scan("closing-tag.php");
    assertThat(check.seen).noneMatch(s -> s.startsWith("/"));
  }

  @Test
  void nonPhpDirectiveIsIgnored() {
    check.seen.clear();
    scan("xml-directive.xml");
    assertThat(check.seen).isEmpty();
  }

  @Test
  void attributeValueWithoutHtmlIsIgnored() {
    scan("no-html.php");
    assertThat(check.seen).isEmpty();
  }
}
