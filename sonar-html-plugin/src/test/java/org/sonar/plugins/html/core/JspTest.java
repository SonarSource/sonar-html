/*
 * SonarQube HTML
 * Copyright (C) 2010-2025 SonarSource Sàrl
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

public class JspTest {

  @Test
  public void testDefaultFileSuffixes() {
    MapSettings settings = new MapSettings();
    settings.setProperty(HtmlConstants.JSP_FILE_EXTENSIONS_PROP_KEY, HtmlConstants.JSP_FILE_EXTENSIONS_DEF_VALUE);
    Jsp jsp = new Jsp(settings.asConfig());
    assertThat(jsp.getFileSuffixes()).containsOnly(".jsp", ".jspf", ".jspx");
  }

  @Test
  public void testCustomFileSuffixes() {
    MapSettings settings = new MapSettings();
    settings.setProperty(HtmlConstants.JSP_FILE_EXTENSIONS_PROP_KEY, "foo, bar ,   toto");
    Jsp jsp = new Jsp(settings.asConfig());
    assertThat(jsp.getFileSuffixes()).containsOnly("foo", "bar", "toto");
  }

}
