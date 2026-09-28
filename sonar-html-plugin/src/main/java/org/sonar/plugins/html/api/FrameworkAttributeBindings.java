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

import java.util.List;
import java.util.stream.Stream;

/**
 * Every spelling Angular and Vue use to bind an attribute or DOM property named {@code propertyName}.
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
  public static List<String> domPropertyBindingNames(String propertyName) {
    return List.of("[" + propertyName + "]", "v-bind:" + propertyName, ":" + propertyName);
  }

  /**
   * Every spelling that can set {@code propertyName}, except Vue's dynamic-argument form
   * ({@code :[x]}), whose target name is only known at runtime:
   * - the plain attribute {@code x}
   * - the {@link #domPropertyBindingNames(String)} DOM-property bindings
   * - the Angular attribute bindings {@code [attr.x]} and {@code attr.x}
   */
  public static List<String> staticAttributeSpellings(String propertyName) {
    return Stream.concat(
      Stream.of(propertyName, "[attr." + propertyName + "]", "attr." + propertyName),
      domPropertyBindingNames(propertyName).stream())
      .toList();
  }

}
