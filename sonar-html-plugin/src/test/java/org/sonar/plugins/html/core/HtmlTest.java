/*
 * SonarQube HTML
 * Copyright (C) 2010-2025 SonarSource SA
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
package org.sonar.plugins.html.core;

import org.junit.jupiter.api.Test;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.plugins.html.api.HtmlConstants;

import static org.assertj.core.api.Assertions.assertThat;

class HtmlTest {

  @Test
  void testDefaultFileSuffixes() {
    MapSettings settings = new MapSettings();
    settings.setProperty(HtmlConstants.FILE_EXTENSIONS_PROP_KEY, HtmlConstants.FILE_EXTENSIONS_DEF_VALUE);
    Html html = new Html(settings.asConfig());
    assertThat(html.getFileSuffixes()).containsOnly(".html", ".xhtml", ".cshtml", ".vbhtml", ".aspx", ".ascx", ".rhtml", ".erb", ".shtm", ".shtml", ".cmp", ".twig");
  }

  @Test
  void testCustomFileSuffixes() {
    MapSettings settings = new MapSettings();
    settings.setProperty(HtmlConstants.FILE_EXTENSIONS_PROP_KEY, "foo, bar ,   toto");
    Html html = new Html(settings.asConfig());
    assertThat(html.getFileSuffixes()).containsOnly("foo", "bar", "toto");
  }

}
