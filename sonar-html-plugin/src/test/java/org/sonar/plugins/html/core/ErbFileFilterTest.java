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

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ErbFileFilterTest {

  @Test
  void skips_non_html_basenames() {
    assertThat(ErbFileFilter.isNonHtmlErb("Dockerfile.erb")).isTrue();
    assertThat(ErbFileFilter.isNonHtmlErb("Makefile.erb")).isTrue();
    assertThat(ErbFileFilter.isNonHtmlErb("Rakefile.erb")).isTrue();
    assertThat(ErbFileFilter.isNonHtmlErb("Gemfile.erb")).isTrue();
    assertThat(ErbFileFilter.isNonHtmlErb("Vagrantfile.erb")).isTrue();
    assertThat(ErbFileFilter.isNonHtmlErb("Procfile.erb")).isTrue();
    assertThat(ErbFileFilter.isNonHtmlErb("Jenkinsfile.erb")).isTrue();
  }

  @Test
  void basename_match_is_case_insensitive() {
    assertThat(ErbFileFilter.isNonHtmlErb("dockerfile.erb")).isTrue();
    assertThat(ErbFileFilter.isNonHtmlErb("DOCKERFILE.erb")).isTrue();
    assertThat(ErbFileFilter.isNonHtmlErb("Dockerfile.ERB")).isTrue();
  }

  @Test
  void skips_non_html_middle_extensions() {
    assertThat(ErbFileFilter.isNonHtmlErb("config.yml.erb")).isTrue();
    assertThat(ErbFileFilter.isNonHtmlErb("settings.yaml.erb")).isTrue();
    assertThat(ErbFileFilter.isNonHtmlErb("notify.text.erb")).isTrue();
    assertThat(ErbFileFilter.isNonHtmlErb("app.js.erb")).isTrue();
    assertThat(ErbFileFilter.isNonHtmlErb("payload.json.erb")).isTrue();
    assertThat(ErbFileFilter.isNonHtmlErb("nginx.conf.erb")).isTrue();
    assertThat(ErbFileFilter.isNonHtmlErb("data.xml.erb")).isTrue();
  }

  @Test
  void keeps_html_like_middle_extensions() {
    assertThat(ErbFileFilter.isNonHtmlErb("index.html.erb")).isFalse();
    assertThat(ErbFileFilter.isNonHtmlErb("page.htm.erb")).isFalse();
    assertThat(ErbFileFilter.isNonHtmlErb("layout.xhtml.erb")).isFalse();
    assertThat(ErbFileFilter.isNonHtmlErb("_partial.HTML.erb")).isFalse();
  }

  @Test
  void keeps_plain_erb_files_without_intermediate_extension() {
    assertThat(ErbFileFilter.isNonHtmlErb("index.erb")).isFalse();
    assertThat(ErbFileFilter.isNonHtmlErb("_user.erb")).isFalse();
    assertThat(ErbFileFilter.isNonHtmlErb("template.erb")).isFalse();
  }

  @Test
  void ignores_non_erb_files() {
    assertThat(ErbFileFilter.isNonHtmlErb("index.html")).isFalse();
    assertThat(ErbFileFilter.isNonHtmlErb("Dockerfile")).isFalse();
    assertThat(ErbFileFilter.isNonHtmlErb("config.yml")).isFalse();
    assertThat(ErbFileFilter.isNonHtmlErb("app.js")).isFalse();
    assertThat(ErbFileFilter.isNonHtmlErb("")).isFalse();
  }
}
