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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.sonar.plugins.html.node.Attribute;
import org.sonar.plugins.html.node.TagNode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.sonar.plugins.html.api.accessibility.AccessibleNameExemption.hasEffectivelyPresentationalRole;
import static org.sonar.plugins.html.api.accessibility.AccessibleNameExemption.isHiddenFromAssistiveTech;

class AccessibleNameExemptionTest {

  @ParameterizedTest(name = "[{index}] onAncestor={0}, attribute={1}={2} -> {3}")
  @MethodSource("hiddenFromAssistiveTechCases")
  void resolvesHiddenFromAssistiveTech(boolean onAncestor, String attributeName, String attributeValue, boolean expected) {
    TagNode svg = svg();
    TagNode target = svg;
    if (onAncestor) {
      target = tag("div");
      svg.setParent(target);
    }
    if (attributeName != null) {
      target.getAttributes().add(new Attribute(attributeName, attributeValue));
    }
    assertThat(isHiddenFromAssistiveTech(svg)).isEqualTo(expected);
  }

  private static Stream<Arguments> hiddenFromAssistiveTechCases() {
    return Stream.of(
      Arguments.of(false, null, null, false),
      Arguments.of(false, "aria-hidden", "true", true),
      Arguments.of(false, "aria-hidden", "false", false),
      Arguments.of(true, "aria-hidden", "true", true),
      Arguments.of(false, "hidden", "", true),
      Arguments.of(true, "hidden", "", true),
      // Angular/Vue bindings are indeterminate at analysis time, so they are treated as hidden
      Arguments.of(false, "[hidden]", "isCollapsed", true),
      Arguments.of(true, ":hidden", "isCollapsed", true),
      Arguments.of(false, "[aria-hidden]", "isDecorative", true),
      Arguments.of(true, ":aria-hidden", "isDecorative", true));
  }

  @ParameterizedTest(name = "[{index}] {0}={1} -> {2}")
  @MethodSource("presentationalRoleCases")
  void resolvesEffectivelyPresentationalRole(String attributeName, String attributeValue, boolean expected) {
    TagNode svg = svg();
    if (attributeName != null) {
      svg.getAttributes().add(new Attribute(attributeName, attributeValue));
    }
    assertThat(hasEffectivelyPresentationalRole(svg)).isEqualTo(expected);
  }

  private static Stream<Arguments> presentationalRoleCases() {
    return Stream.of(
      Arguments.of(null, null, false),
      Arguments.of("role", "", false),
      Arguments.of("role", "presentation", true),
      Arguments.of("role", "none", true),
      Arguments.of("role", "img", false),
      // the fallback list is read left to right; the first valid, non-abstract token wins
      Arguments.of("role", "unknown-role presentation", true),
      Arguments.of("role", "unknown-role", false),
      // an abstract role is skipped, same as an unknown one
      Arguments.of("role", "widget presentation", true),
      // Angular/Vue bindings are indeterminate at analysis time, so they are treated as presentational
      Arguments.of("[role]", "dynamicRole", true),
      Arguments.of(":role", "dynamicRole", true));
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
