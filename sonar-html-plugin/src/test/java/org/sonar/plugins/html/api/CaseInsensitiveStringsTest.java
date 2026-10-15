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
import java.util.Locale;
import java.util.Set;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CaseInsensitiveStringsTest {

  @Test
  void newCaseInsensitiveSetIsMutable() {
    Set<String> set = CaseInsensitiveStrings.newCaseInsensitiveSet();
    set.add("Foo");
    assertThat(set.add("FOO")).isFalse();
    assertThat(set).containsExactly("Foo");
  }

  @Test
  void caseInsensitiveSetOfValues() {
    Set<String> set = CaseInsensitiveStrings.caseInsensitiveSetOf("a", "B");
    assertThat(set).containsExactlyInAnyOrder("a", "B");
    assertThat(List.of("A", "b")).allMatch(set::contains);
    assertThatThrownBy(() -> set.add("c")).isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  void caseInsensitiveSetOfBaseAndExtra() {
    Set<String> set = CaseInsensitiveStrings.caseInsensitiveSetOf(List.of("a", "b"), "C");
    assertThat(set).containsExactlyInAnyOrder("a", "b", "C");
    assertThat(List.of("A", "B", "c")).allMatch(set::contains);
    assertThatThrownBy(() -> set.add("d")).isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  void caseInsensitiveSetOfBases() {
    Set<String> set = CaseInsensitiveStrings.caseInsensitiveSetOf(List.of("a"), Set.of("B", "c"));
    assertThat(set).containsExactlyInAnyOrder("a", "B", "c");
    assertThat(List.of("A", "b", "C")).allMatch(set::contains);
    assertThatThrownBy(() -> set.add("d")).isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  void matchingIsLocaleIndependent() {
    Locale original = Locale.getDefault();
    try {
      // Turkish: 'i'/'I' case-fold to 'İ'/'ı', not 'I'/'i'
      Locale.setDefault(Locale.forLanguageTag("tr"));
      assertThat(List.of("TITLE")).allMatch(CaseInsensitiveStrings.caseInsensitiveSetOf("title")::contains);
    } finally {
      Locale.setDefault(original);
    }
  }

}
