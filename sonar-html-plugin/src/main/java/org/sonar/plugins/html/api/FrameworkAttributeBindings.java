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

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

/**
 * Every spelling Angular and Vue use to bind an attribute or DOM property named {@code propertyName}.
 * The returned sets match case-insensitively and locale-independently, like HTML attribute names.
 */
public final class FrameworkAttributeBindings {

  private FrameworkAttributeBindings() {
    // utility class
  }

  /**
   * The spellings that bind the DOM property {@code propertyName} itself: Angular {@code [x]},
   * Vue {@code v-bind:x} and {@code :x}. Excludes the attribute bindings ({@code [attr.x]}/{@code attr.x})
   * matched by {@link #staticAttributeSpellings(String)}, which write an HTML attribute instead.
   */
  public static Set<String> domPropertyBindingNames(String propertyName) {
    return caseInsensitiveSetOf("[" + propertyName + "]", "v-bind:" + propertyName, ":" + propertyName);
  }

  /**
   * Every spelling that can set {@code propertyName}, except Vue's dynamic-argument form
   * ({@code :[x]}), whose target name is only known at runtime:
   * - the plain attribute {@code x}
   * - the {@link #domPropertyBindingNames(String)} DOM-property bindings
   * - the Angular attribute bindings {@code [attr.x]} and {@code attr.x}
   */
  public static Set<String> staticAttributeSpellings(String propertyName) {
    Set<String> spellings = newCaseInsensitiveSet();
    Collections.addAll(spellings, propertyName, "[attr." + propertyName + "]", "attr." + propertyName);
    spellings.addAll(domPropertyBindingNames(propertyName));
    return Collections.unmodifiableSet(spellings);
  }

  /**
   * An empty, mutable set matching like the ones returned here, for callers combining spellings.
   * {@link String#CASE_INSENSITIVE_ORDER} is locale-independent, unlike a bare {@code toLowerCase()}.
   */
  public static Set<String> newCaseInsensitiveSet() {
    return new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
  }

  private static Set<String> caseInsensitiveSetOf(String... values) {
    Set<String> set = newCaseInsensitiveSet();
    set.addAll(Arrays.asList(values));
    return Collections.unmodifiableSet(set);
  }

}
