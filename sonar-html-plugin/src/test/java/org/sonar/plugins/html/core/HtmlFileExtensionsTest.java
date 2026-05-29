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
import org.sonar.api.config.internal.MapSettings;
import org.sonar.plugins.html.api.HtmlConstants;

import static org.assertj.core.api.Assertions.assertThat;

class HtmlFileExtensionsTest {

  @Test
  void includes_user_configured_html_and_jsp_suffixes() {
    MapSettings settings = new MapSettings();
    settings.setProperty(HtmlConstants.FILE_EXTENSIONS_PROP_KEY, ".html,.htm,.erb");
    settings.setProperty(HtmlConstants.JSP_FILE_EXTENSIONS_PROP_KEY, ".jsp,.jspx");

    Set<String> ext = HtmlFileExtensions.recognized(settings.asConfig());

    assertThat(ext).contains("html", "htm", "erb", "jsp", "jspx");
  }

  @Test
  void always_includes_other_file_suffixes() {
    Set<String> ext = HtmlFileExtensions.recognized(new MapSettings().asConfig());

    assertThat(ext).containsAll(HtmlConstants.OTHER_FILE_SUFFIXES);
  }

  @Test
  void normalizes_leading_dot_and_case() {
    MapSettings settings = new MapSettings();
    settings.setProperty(HtmlConstants.FILE_EXTENSIONS_PROP_KEY, ".HTML, .CSHTML , twig");

    Set<String> ext = HtmlFileExtensions.recognized(settings.asConfig());

    assertThat(ext).contains("html", "cshtml", "twig");
  }

  @Test
  void ignores_empty_entries() {
    MapSettings settings = new MapSettings();
    settings.setProperty(HtmlConstants.FILE_EXTENSIONS_PROP_KEY, ",, ,html,");

    Set<String> ext = HtmlFileExtensions.recognized(settings.asConfig());

    assertThat(ext).contains("html").doesNotContain("");
  }
}
