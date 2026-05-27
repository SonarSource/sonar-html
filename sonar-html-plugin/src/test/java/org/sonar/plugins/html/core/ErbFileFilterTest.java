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

  @Test
  void skips_erb_without_intermediate_extension() {
    assertThat(ErbFileFilter.shouldSkip("Dockerfile.erb", RECOGNIZED)).isTrue();
    assertThat(ErbFileFilter.shouldSkip("Makefile.erb", RECOGNIZED)).isTrue();
    assertThat(ErbFileFilter.shouldSkip("Rakefile.erb", RECOGNIZED)).isTrue();
    assertThat(ErbFileFilter.shouldSkip("template.erb", RECOGNIZED)).isTrue();
    assertThat(ErbFileFilter.shouldSkip("_partial.erb", RECOGNIZED)).isTrue();
  }

  @Test
  void skips_erb_with_unrecognized_intermediate_extension() {
    assertThat(ErbFileFilter.shouldSkip("config.yml.erb", RECOGNIZED)).isTrue();
    assertThat(ErbFileFilter.shouldSkip("settings.yaml.erb", RECOGNIZED)).isTrue();
    assertThat(ErbFileFilter.shouldSkip("notify.text.erb", RECOGNIZED)).isTrue();
    assertThat(ErbFileFilter.shouldSkip("app.js.erb", RECOGNIZED)).isTrue();
    assertThat(ErbFileFilter.shouldSkip("payload.json.erb", RECOGNIZED)).isTrue();
    assertThat(ErbFileFilter.shouldSkip("nginx.conf.erb", RECOGNIZED)).isTrue();
    assertThat(ErbFileFilter.shouldSkip("data.xml.erb", RECOGNIZED)).isTrue();
  }

  @Test
  void keeps_erb_with_recognized_html_intermediate_extension() {
    assertThat(ErbFileFilter.shouldSkip("index.html.erb", RECOGNIZED)).isFalse();
    assertThat(ErbFileFilter.shouldSkip("_partial.html.erb", RECOGNIZED)).isFalse();
    assertThat(ErbFileFilter.shouldSkip("page.htm.erb", RECOGNIZED)).isFalse();
    assertThat(ErbFileFilter.shouldSkip("layout.xhtml.erb", RECOGNIZED)).isFalse();
    assertThat(ErbFileFilter.shouldSkip("view.cshtml.erb", RECOGNIZED)).isFalse();
    assertThat(ErbFileFilter.shouldSkip("page.aspx.erb", RECOGNIZED)).isFalse();
  }

  @Test
  void keeps_erb_with_recognized_jsp_intermediate_extension() {
    assertThat(ErbFileFilter.shouldSkip("view.jsp.erb", RECOGNIZED)).isFalse();
    assertThat(ErbFileFilter.shouldSkip("fragment.jspf.erb", RECOGNIZED)).isFalse();
  }

  @Test
  void keeps_erb_with_recognized_other_intermediate_extension() {
    assertThat(ErbFileFilter.shouldSkip("script.php.erb", RECOGNIZED)).isFalse();
    assertThat(ErbFileFilter.shouldSkip("script.phtml.erb", RECOGNIZED)).isFalse();
    assertThat(ErbFileFilter.shouldSkip("component.vue.erb", RECOGNIZED)).isFalse();
  }

  @Test
  void match_is_case_insensitive() {
    assertThat(ErbFileFilter.shouldSkip("Dockerfile.ERB", RECOGNIZED)).isTrue();
    assertThat(ErbFileFilter.shouldSkip("index.HTML.erb", RECOGNIZED)).isFalse();
    assertThat(ErbFileFilter.shouldSkip("INDEX.HTML.ERB", RECOGNIZED)).isFalse();
  }

  @Test
  void ignores_non_erb_files() {
    assertThat(ErbFileFilter.shouldSkip("index.html", RECOGNIZED)).isFalse();
    assertThat(ErbFileFilter.shouldSkip("Dockerfile", RECOGNIZED)).isFalse();
    assertThat(ErbFileFilter.shouldSkip("config.yml", RECOGNIZED)).isFalse();
    assertThat(ErbFileFilter.shouldSkip("app.js", RECOGNIZED)).isFalse();
    assertThat(ErbFileFilter.shouldSkip("", RECOGNIZED)).isFalse();
  }

  @Test
  void respects_caller_provided_extension_set() {
    // a caller can add custom extensions (e.g. user-configured suffixes)
    Set<String> custom = Set.of("html", "custom");
    assertThat(ErbFileFilter.shouldSkip("foo.custom.erb", custom)).isFalse();
    assertThat(ErbFileFilter.shouldSkip("foo.vue.erb", custom)).isTrue();
  }
}
