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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import org.sonar.api.config.Configuration;
import org.sonar.plugins.html.api.HtmlConstants;

/**
 * What file extensions sonar-html recognizes as HTML-bearing.
 * Single source of truth for any code that needs to make that decision.
 */
public final class HtmlFileExtensions {

  private HtmlFileExtensions() {
  }

  /**
   * Union of HTML language suffixes, JSP language suffixes and
   * {@link HtmlConstants#OTHER_FILE_SUFFIXES}, normalized to lowercase without a
   * leading dot. Defaults flow in via the property definitions registered in
   * {@code HtmlPlugin.pluginProperties()}, so {@code getStringArray} returns them
   * when no user override is set.
   */
  public static Set<String> recognized(Configuration config) {
    Set<String> exts = new HashSet<>();
    addExtensions(exts, Arrays.asList(config.getStringArray(HtmlConstants.FILE_EXTENSIONS_PROP_KEY)));
    addExtensions(exts, Arrays.asList(config.getStringArray(HtmlConstants.JSP_FILE_EXTENSIONS_PROP_KEY)));
    addExtensions(exts, HtmlConstants.OTHER_FILE_SUFFIXES);
    return exts;
  }

  private static void addExtensions(Set<String> sink, Iterable<String> extensions) {
    for (String ext : extensions) {
      String trimmed = ext.trim();
      if (trimmed.isEmpty()) {
        continue;
      }
      String noDot = trimmed.startsWith(".") ? trimmed.substring(1) : trimmed;
      sink.add(noDot.toLowerCase(Locale.ROOT));
    }
  }
}
