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
package org.sonar.plugins.html.core;

import java.util.Set;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ErbFileFilterTest {

  private static final Set<String> RECOGNIZED = Set.of(
    "html", "htm", "xhtml", "cshtml", "vbhtml", "aspx", "ascx", "rhtml",
    "erb", "shtm", "shtml", "cmp", "twig",
    "jsp", "jspf", "jspx",
    "php", "php3", "php4", "php5", "phtml", "inc", "vue"
  );

  // hasRecognizedDoubleExtension — purely filename-based, no I/O.

  @Test
  void bare_erb_has_no_recognized_double_extension() {
    assertThat(ErbFileFilter.hasRecognizedDoubleExtension("dockerfile.erb", RECOGNIZED)).isFalse();
    assertThat(ErbFileFilter.hasRecognizedDoubleExtension("makefile.erb", RECOGNIZED)).isFalse();
    assertThat(ErbFileFilter.hasRecognizedDoubleExtension("rakefile.erb", RECOGNIZED)).isFalse();
    assertThat(ErbFileFilter.hasRecognizedDoubleExtension("template.erb", RECOGNIZED)).isFalse();
    assertThat(ErbFileFilter.hasRecognizedDoubleExtension("_partial.erb", RECOGNIZED)).isFalse();
  }

  @Test
  void unrecognized_intermediate_extension_is_not_recognized() {
    assertThat(ErbFileFilter.hasRecognizedDoubleExtension("config.yml.erb", RECOGNIZED)).isFalse();
    assertThat(ErbFileFilter.hasRecognizedDoubleExtension("settings.yaml.erb", RECOGNIZED)).isFalse();
    assertThat(ErbFileFilter.hasRecognizedDoubleExtension("notify.text.erb", RECOGNIZED)).isFalse();
    assertThat(ErbFileFilter.hasRecognizedDoubleExtension("app.js.erb", RECOGNIZED)).isFalse();
    assertThat(ErbFileFilter.hasRecognizedDoubleExtension("payload.json.erb", RECOGNIZED)).isFalse();
    assertThat(ErbFileFilter.hasRecognizedDoubleExtension("nginx.conf.erb", RECOGNIZED)).isFalse();
    assertThat(ErbFileFilter.hasRecognizedDoubleExtension("data.xml.erb", RECOGNIZED)).isFalse();
  }

  @Test
  void recognized_html_intermediate_extension_is_recognized() {
    assertThat(ErbFileFilter.hasRecognizedDoubleExtension("index.html.erb", RECOGNIZED)).isTrue();
    assertThat(ErbFileFilter.hasRecognizedDoubleExtension("_partial.html.erb", RECOGNIZED)).isTrue();
    assertThat(ErbFileFilter.hasRecognizedDoubleExtension("page.htm.erb", RECOGNIZED)).isTrue();
    assertThat(ErbFileFilter.hasRecognizedDoubleExtension("layout.xhtml.erb", RECOGNIZED)).isTrue();
    assertThat(ErbFileFilter.hasRecognizedDoubleExtension("view.cshtml.erb", RECOGNIZED)).isTrue();
    assertThat(ErbFileFilter.hasRecognizedDoubleExtension("page.aspx.erb", RECOGNIZED)).isTrue();
  }

  @Test
  void recognized_jsp_intermediate_extension_is_recognized() {
    assertThat(ErbFileFilter.hasRecognizedDoubleExtension("view.jsp.erb", RECOGNIZED)).isTrue();
    assertThat(ErbFileFilter.hasRecognizedDoubleExtension("fragment.jspf.erb", RECOGNIZED)).isTrue();
  }

  @Test
  void recognized_other_intermediate_extension_is_recognized() {
    assertThat(ErbFileFilter.hasRecognizedDoubleExtension("script.php.erb", RECOGNIZED)).isTrue();
    assertThat(ErbFileFilter.hasRecognizedDoubleExtension("script.phtml.erb", RECOGNIZED)).isTrue();
    assertThat(ErbFileFilter.hasRecognizedDoubleExtension("component.vue.erb", RECOGNIZED)).isTrue();
  }

  @Test
  void caller_provided_extension_set_is_respected() {
    Set<String> custom = Set.of("html", "custom");
    assertThat(ErbFileFilter.hasRecognizedDoubleExtension("foo.custom.erb", custom)).isTrue();
    assertThat(ErbFileFilter.hasRecognizedDoubleExtension("foo.vue.erb", custom)).isFalse();
  }

  // looksLikeHtml — content sniff after stripping ERB code blocks.

  @Test
  void strong_html_markers_are_detected() {
    assertThat(ErbFileFilter.looksLikeHtml("<!DOCTYPE html><body>hi</body>")).isTrue();
    assertThat(ErbFileFilter.looksLikeHtml("<html><head><title>t</title></head></html>")).isTrue();
    assertThat(ErbFileFilter.looksLikeHtml("<head><meta charset=\"utf-8\"></head>")).isTrue();
    assertThat(ErbFileFilter.looksLikeHtml("<body>just a body</body>")).isTrue();
  }

  @Test
  void two_weak_html_tags_are_enough() {
    assertThat(ErbFileFilter.looksLikeHtml("<div class=\"x\">one</div><span>two</span>")).isTrue();
    assertThat(ErbFileFilter.looksLikeHtml("<p>line</p>\n<a href=\"/\">link</a>")).isTrue();
  }

  @Test
  void single_weak_html_tag_is_not_enough() {
    assertThat(ErbFileFilter.looksLikeHtml("Some text with a single <p> tag.")).isFalse();
  }

  @Test
  void html_markers_inside_erb_blocks_do_not_count() {
    // ERB code that references HTML tag names should NOT make the file look like HTML.
    assertThat(ErbFileFilter.looksLikeHtml("<% render '<html>' %>")).isFalse();
    assertThat(ErbFileFilter.looksLikeHtml("<% if cond %><% else %>plain text<% end %>")).isFalse();
    // Multiline ERB block guarding strong markers should also be stripped.
    assertThat(ErbFileFilter.looksLikeHtml("<%\nputs '<html>'\nputs '<body>'\n%>plain")).isFalse();
  }

  @Test
  void non_html_content_is_rejected() {
    assertThat(ErbFileFilter.looksLikeHtml("FROM ubuntu:<%= version %>\nRUN apt-get update")).isFalse();
    assertThat(ErbFileFilter.looksLikeHtml("server:\n  host: <%= host %>\n  port: 80")).isFalse();
    assertThat(ErbFileFilter.looksLikeHtml("Hello <%= name %>, you have <%= count %> messages.")).isFalse();
    assertThat(ErbFileFilter.looksLikeHtml("")).isFalse();
  }

  @Test
  void matching_is_case_insensitive() {
    assertThat(ErbFileFilter.looksLikeHtml("<HTML><BODY></BODY></HTML>")).isTrue();
    assertThat(ErbFileFilter.looksLikeHtml("<!doctype HTML>\n<div>a</div>")).isTrue();
    assertThat(ErbFileFilter.hasRecognizedDoubleExtension("INDEX.HTML.ERB".toLowerCase(java.util.Locale.ROOT), RECOGNIZED)).isTrue();
  }
}
