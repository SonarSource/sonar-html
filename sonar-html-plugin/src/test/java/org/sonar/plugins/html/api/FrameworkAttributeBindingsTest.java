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

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FrameworkAttributeBindingsTest {

  @Test
  void domPropertyBindingNames() {
    assertThat(FrameworkAttributeBindings.domPropertyBindingNames("scope"))
      .containsExactlyInAnyOrder("[scope]", "v-bind:scope", ":scope");
  }

  @Test
  void staticAttributeSpellings() {
    assertThat(FrameworkAttributeBindings.staticAttributeSpellings("scope"))
      .containsExactlyInAnyOrder("scope", "[attr.scope]", "attr.scope", "[scope]", "v-bind:scope", ":scope")
      // the dynamic-argument form's bound name is only known at runtime, so it is not a "scope" spelling
      .doesNotContain(":[scope]");
  }

}
