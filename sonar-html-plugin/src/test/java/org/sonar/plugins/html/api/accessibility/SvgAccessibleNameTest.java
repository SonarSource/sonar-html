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
package org.sonar.plugins.html.api.accessibility;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.sonar.plugins.html.node.Attribute;
import org.sonar.plugins.html.node.TagNode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.sonar.plugins.html.api.accessibility.SvgAccessibleName.hasEffectivelyPresentationalRole;
import static org.sonar.plugins.html.api.accessibility.SvgAccessibleName.isHiddenFromAssistiveTech;

class SvgAccessibleNameTest {

  @Test
  void notHiddenByDefault() {
    assertThat(isHiddenFromAssistiveTech(svg())).isFalse();
  }

  @Test
  void hiddenViaOwnAriaHidden() {
    TagNode svg = svg();
    svg.getAttributes().add(new Attribute("aria-hidden", "true"));
    assertThat(isHiddenFromAssistiveTech(svg)).isTrue();
  }

  @Test
  void notHiddenWhenAriaHiddenIsFalse() {
    TagNode svg = svg();
    svg.getAttributes().add(new Attribute("aria-hidden", "false"));
    assertThat(isHiddenFromAssistiveTech(svg)).isFalse();
  }

  @Test
  void hiddenViaInheritedAriaHidden() {
    TagNode ancestor = tag("div");
    ancestor.getAttributes().add(new Attribute("aria-hidden", "true"));
    TagNode svg = svg();
    svg.setParent(ancestor);
    assertThat(isHiddenFromAssistiveTech(svg)).isTrue();
  }

  @Test
  void indeterminateWhenOwnAriaHiddenIsAngularBound() {
    TagNode svg = svg();
    svg.getAttributes().add(new Attribute("[aria-hidden]", "isDecorative"));
    assertThat(isHiddenFromAssistiveTech(svg)).isTrue();
  }

  @Test
  void indeterminateWhenAncestorAriaHiddenIsVueBound() {
    TagNode ancestor = tag("div");
    ancestor.getAttributes().add(new Attribute(":aria-hidden", "isDecorative"));
    TagNode svg = svg();
    svg.setParent(ancestor);
    assertThat(isHiddenFromAssistiveTech(svg)).isTrue();
  }

  @ParameterizedTest
  @MethodSource("presentationalRoleCases")
  void resolvesEffectivelyPresentationalRole(String roleAttributeValue, boolean expected) {
    TagNode svg = svg();
    if (roleAttributeValue != null) {
      svg.getAttributes().add(new Attribute("role", roleAttributeValue));
    }
    assertThat(hasEffectivelyPresentationalRole(svg)).isEqualTo(expected);
  }

  private static Stream<Arguments> presentationalRoleCases() {
    return Stream.of(
      Arguments.of((String) null, false),
      Arguments.of("", false),
      Arguments.of("presentation", true),
      Arguments.of("none", true),
      Arguments.of("img", false),
      // the fallback list is read left to right; the first valid, non-abstract token wins
      Arguments.of("unknown-role presentation", true),
      Arguments.of("unknown-role", false),
      // an abstract role is skipped, same as an unknown one
      Arguments.of("widget presentation", true));
  }

  @Test
  void indeterminateWhenRoleIsAngularBound() {
    TagNode svg = svg();
    svg.getAttributes().add(new Attribute("[role]", "dynamicRole"));
    assertThat(hasEffectivelyPresentationalRole(svg)).isTrue();
  }

  @Test
  void indeterminateWhenRoleIsVueBound() {
    TagNode svg = svg();
    svg.getAttributes().add(new Attribute(":role", "dynamicRole"));
    assertThat(hasEffectivelyPresentationalRole(svg)).isTrue();
  }

  private static TagNode svg() {
    return tag("svg");
  }

  private static TagNode tag(String name) {
    TagNode node = new TagNode();
    node.setNodeName(name);
    return node;
  }
}
