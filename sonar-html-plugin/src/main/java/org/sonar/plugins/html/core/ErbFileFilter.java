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

import java.util.Locale;
import java.util.Set;

// ERB is Ruby's general-purpose template system, used for HTML but also for
// Dockerfiles, YAML, plain text, and so on. The HTML plugin must not claim
// files whose ERB output is not HTML, otherwise it produces parse errors and
// clashes with the plugin that owns that content (e.g. sonar-iac for Dockerfile).
public final class ErbFileFilter {

  private static final String ERB_SUFFIX = ".erb";

  private static final Set<String> NON_HTML_BASENAMES = Set.of(
    "dockerfile",
    "makefile",
    "rakefile",
    "gemfile",
    "brewfile",
    "vagrantfile",
    "procfile",
    "capfile",
    "berksfile",
    "guardfile",
    "jenkinsfile"
  );

  private static final Set<String> HTML_LIKE_MIDDLE_EXTENSIONS = Set.of(
    "html",
    "htm",
    "xhtml"
  );

  private ErbFileFilter() {
  }

  /**
   * Tells whether an ERB template file is likely not HTML and should be skipped.
   *
   * @param filename the filename to inspect, including its extension
   * @return true if the file is a non-HTML ERB template
   */
  public static boolean isNonHtmlErb(String filename) {
    String lower = filename.toLowerCase(Locale.ROOT);
    if (!lower.endsWith(ERB_SUFFIX)) {
      return false;
    }
    String beforeErb = filename.substring(0, filename.length() - ERB_SUFFIX.length());
    if (NON_HTML_BASENAMES.contains(beforeErb.toLowerCase(Locale.ROOT))) {
      return true;
    }
    int dotIdx = beforeErb.lastIndexOf('.');
    if (dotIdx < 0) {
      return false;
    }
    String middleExt = beforeErb.substring(dotIdx + 1).toLowerCase(Locale.ROOT);
    return !HTML_LIKE_MIDDLE_EXTENSIONS.contains(middleExt);
  }
}
