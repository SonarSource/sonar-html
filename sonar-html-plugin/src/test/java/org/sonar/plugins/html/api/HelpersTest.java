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

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

import static org.assertj.core.api.Assertions.assertThat;

class HelpersTest {

  private static HtmlSourceCode sourceCode(String filename) {
    return new HtmlSourceCode(
      new TestInputFileBuilder("key", filename)
        .setLanguage(HtmlConstants.LANGUAGE_KEY)
        .setType(InputFile.Type.MAIN)
        .setCharset(StandardCharsets.UTF_8)
        .build()
    );
  }

  @Test
  void contains_dynamic_value_detects_each_marker() {
    HtmlSourceCode code = sourceCode("file.html");
    assertThat(Helpers.containsDynamicValue("Welcome ${name}", code)).isTrue();
    assertThat(Helpers.containsDynamicValue("Welcome #{user.name}", code)).isTrue();
    assertThat(Helpers.containsDynamicValue("intro {{ greeting }} outro", code)).isTrue();
    assertThat(Helpers.containsDynamicValue("intro {% if x %} outro", code)).isTrue();
    assertThat(Helpers.containsDynamicValue("hello <?= $user ?> bye", code)).isTrue();
    assertThat(Helpers.containsDynamicValue("hello <?php echo $u; ?> bye", code)).isTrue();
    assertThat(Helpers.containsDynamicValue("hello <% scriptlet %> bye", code)).isTrue();
    assertThat(Helpers.containsDynamicValue("hello <%= expr %> bye", code)).isTrue();
  }

  @Test
  void contains_server_side_marker_matches_supported_processing_instructions_only() {
    assertThat(Helpers.containsServerSideMarker("<?php echo $u; ?>")).isTrue();
    assertThat(Helpers.containsServerSideMarker("<?= $user ?>")).isTrue();
    assertThat(Helpers.containsServerSideMarker("<?xml version=\"1.0\"?>")).isFalse();
    assertThat(Helpers.containsServerSideMarker("<?foo \"bar\" ?>")).isFalse();
  }

  @Test
  void contains_dynamic_value_returns_false_when_no_marker_present() {
    HtmlSourceCode code = sourceCode("file.html");
    assertThat(Helpers.containsDynamicValue("just a static string", code)).isFalse();
    assertThat(Helpers.containsDynamicValue("", code)).isFalse();
    assertThat(Helpers.containsDynamicValue("uses $ and { but not together", code)).isFalse();
  }

  @Test
  void contains_dynamic_value_detects_razor_for_cshtml_and_vbhtml() {
    String razor = "Hello @user.Name";
    assertThat(Helpers.containsDynamicValue(razor, sourceCode("file.cshtml"))).isTrue();
    assertThat(Helpers.containsDynamicValue(razor, sourceCode("file.vbhtml"))).isTrue();
    assertThat(Helpers.containsDynamicValue(razor, sourceCode("file.html"))).isFalse();
  }

  @Test
  void contains_dynamic_value_ignores_escaped_razor_at() {
    assertThat(Helpers.containsDynamicValue("user@@example.com", sourceCode("file.cshtml"))).isFalse();
    assertThat(Helpers.containsDynamicValue("user@@example.com", sourceCode("file.vbhtml"))).isFalse();
  }

  @Test
  void is_server_side_file_recognizes_template_suffixes() {
    assertThat(Helpers.isServerSideFile(sourceCode("page.jsp"))).isTrue();
    assertThat(Helpers.isServerSideFile(sourceCode("page.jspf"))).isTrue();
    assertThat(Helpers.isServerSideFile(sourceCode("page.jspx"))).isTrue();
    assertThat(Helpers.isServerSideFile(sourceCode("page.php"))).isTrue();
    assertThat(Helpers.isServerSideFile(sourceCode("page.phtml"))).isTrue();
    assertThat(Helpers.isServerSideFile(sourceCode("page.cshtml"))).isTrue();
    assertThat(Helpers.isServerSideFile(sourceCode("page.vbhtml"))).isTrue();
    assertThat(Helpers.isServerSideFile(sourceCode("page.aspx"))).isTrue();
    assertThat(Helpers.isServerSideFile(sourceCode("page.ascx"))).isTrue();
    assertThat(Helpers.isServerSideFile(sourceCode("page.erb"))).isTrue();
    assertThat(Helpers.isServerSideFile(sourceCode("page.html.erb"))).isTrue();
    assertThat(Helpers.isServerSideFile(sourceCode("page.rhtml"))).isTrue();
    assertThat(Helpers.isServerSideFile(sourceCode("page.twig"))).isTrue();
    assertThat(Helpers.isServerSideFile(sourceCode("page.shtml"))).isTrue();
    assertThat(Helpers.isServerSideFile(sourceCode("page.xhtml"))).isTrue();
  }

  @Test
  void is_server_side_file_returns_false_for_static_html() {
    assertThat(Helpers.isServerSideFile(sourceCode("page.html"))).isFalse();
    assertThat(Helpers.isServerSideFile(sourceCode("page.htm"))).isFalse();
    assertThat(Helpers.isServerSideFile(sourceCode("page.xml"))).isFalse();
  }
}
