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

// ERB is Ruby's general-purpose template engine and is used for many non-HTML
// outputs (Dockerfile, YAML, plain text, ...). We only want to analyze .erb
// files whose intermediate extension matches a format sonar-html already
// recognizes (e.g. `foo.html.erb`, `foo.php.erb`, `foo.vue.erb`). A bare
// `foo.erb` carries no signal about its content type and is skipped.
public final class ErbFileFilter {

  private static final String ERB_SUFFIX = ".erb";

  private ErbFileFilter() {
  }

  /**
   * Tells whether an ERB template should be skipped because it lacks a recognized
   * intermediate extension before {@code .erb}.
   *
   * @param filename the filename to inspect, including its extension
   * @param recognizedExtensions extensions sonar-html knows about, lowercase and without leading dot
   * @return true if the file is a .erb file without a recognized double extension
   */
  public static boolean shouldSkip(String filename, Set<String> recognizedExtensions) {
    String lower = filename.toLowerCase(Locale.ROOT);
    if (!lower.endsWith(ERB_SUFFIX)) {
      return false;
    }
    String beforeErb = lower.substring(0, lower.length() - ERB_SUFFIX.length());
    int dotIdx = beforeErb.lastIndexOf('.');
    if (dotIdx < 0) {
      return true;
    }
    String middleExt = beforeErb.substring(dotIdx + 1);
    return !recognizedExtensions.contains(middleExt);
  }
}
