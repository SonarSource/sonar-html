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

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

/**
 * Sets of strings matching case-insensitively and locale-independently, like HTML attribute names.
 * {@link String#CASE_INSENSITIVE_ORDER} is locale-independent, unlike a bare {@code toLowerCase()}.
 */
public final class CaseInsensitiveStrings {

  private CaseInsensitiveStrings() {
    // utility class
  }

  /**
   * An empty, mutable case-insensitive set, for callers building one up incrementally.
   */
  public static Set<String> newCaseInsensitiveSet() {
    return new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
  }

  /**
   * An unmodifiable case-insensitive set of {@code values}.
   */
  public static Set<String> caseInsensitiveSetOf(String... values) {
    Set<String> set = newCaseInsensitiveSet();
    Collections.addAll(set, values);
    return Collections.unmodifiableSet(set);
  }

  /**
   * An unmodifiable case-insensitive set of {@code base} plus {@code extra}.
   */
  public static Set<String> caseInsensitiveSetOf(Collection<String> base, String... extra) {
    Set<String> set = newCaseInsensitiveSet();
    set.addAll(base);
    Collections.addAll(set, extra);
    return Collections.unmodifiableSet(set);
  }

  /**
   * An unmodifiable case-insensitive set of every entry of {@code bases}.
   */
  @SafeVarargs
  public static Set<String> caseInsensitiveSetOf(Collection<String>... bases) {
    Set<String> set = newCaseInsensitiveSet();
    for (Collection<String> base : bases) {
      set.addAll(base);
    }
    return Collections.unmodifiableSet(set);
  }

}
