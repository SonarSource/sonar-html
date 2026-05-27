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

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.sonar.plugins.html.node.Attribute;
import org.sonar.plugins.html.node.TagNode;

import static org.assertj.core.api.Assertions.assertThat;

class ThymeleafTest {

  @Test
  void hasAttrAssignment_returnsFalse_whenThAttrIsAbsent() {
    assertThat(Thymeleaf.hasAttrAssignment(tagWithoutThAttr(), "alt")).isFalse();
  }

  @Test
  void hasAttrAssignment_returnsTrue_whenAssignmentExists() {
    assertThat(Thymeleaf.hasAttrAssignment(tagWithThAttr("alt=#{logo}"), "alt")).isTrue();
  }

  @Test
  void hasAttrAssignment_returnsFalse_whenAssignmentIsMissing() {
    assertThat(Thymeleaf.hasAttrAssignment(tagWithThAttr("src=@{/logo.png}"), "alt")).isFalse();
  }

  @Test
  void hasAttrAssignment_isCaseInsensitiveOnAttributeName() {
    assertThat(Thymeleaf.hasAttrAssignment(tagWithThAttr("ALT=#{logo}"), "alt")).isTrue();
    assertThat(Thymeleaf.hasAttrAssignment(tagWithThAttr("aria-Label=#{l}"), "aria-label")).isTrue();
  }

  @Test
  void getAttrAssignmentValue_returnsNull_whenThAttrIsAbsent() {
    assertThat(Thymeleaf.getAttrAssignmentValue(tagWithoutThAttr(), "alt")).isNull();
  }

  @Test
  void getAttrAssignmentValue_returnsExpressionValue() {
    assertThat(Thymeleaf.getAttrAssignmentValue(tagWithThAttr("alt=#{logo}"), "alt")).isEqualTo("#{logo}");
  }

  @Test
  void getAttrAssignmentValue_returnsQuotedLiteral() {
    assertThat(Thymeleaf.getAttrAssignmentValue(tagWithThAttr("alt=''"), "alt")).isEqualTo("''");
    assertThat(Thymeleaf.getAttrAssignmentValue(tagWithThAttr("alt='Hello'"), "alt")).isEqualTo("'Hello'");
  }

  @Test
  void getAttrAssignmentValue_picksRightAssignmentAmongMany() {
    String thAttr = "src=@{/logo.png}, title=#{logo}, alt=#{label}";
    assertThat(Thymeleaf.getAttrAssignmentValue(tagWithThAttr(thAttr), "alt")).isEqualTo("#{label}");
    assertThat(Thymeleaf.getAttrAssignmentValue(tagWithThAttr(thAttr), "title")).isEqualTo("#{logo}");
    assertThat(Thymeleaf.getAttrAssignmentValue(tagWithThAttr(thAttr), "src")).isEqualTo("@{/logo.png}");
  }

  @ParameterizedTest
  @MethodSource("thAttrValuesWithEmbeddedCommas")
  void getAttrAssignmentValue_ignoresCommasAndEscapedQuotes(String thAttr, String expectedAltValue) {
    assertThat(Thymeleaf.getAttrAssignmentValue(tagWithThAttr(thAttr), "alt")).isEqualTo(expectedAltValue);
    assertThat(Thymeleaf.getAttrAssignmentValue(tagWithThAttr(thAttr), "title")).isEqualTo("#{t}");
  }

  @Test
  void isEmptyAssignmentValue_emptyString() {
    assertThat(Thymeleaf.isEmptyAssignmentValue("")).isTrue();
  }

  @Test
  void isEmptyAssignmentValue_emptyQuotedLiteral() {
    assertThat(Thymeleaf.isEmptyAssignmentValue("''")).isTrue();
    assertThat(Thymeleaf.isEmptyAssignmentValue("\"\"")).isTrue();
  }

  @Test
  void isEmptyAssignmentValue_whitespaceQuotedLiteral() {
    assertThat(Thymeleaf.isEmptyAssignmentValue("' '")).isTrue();
    assertThat(Thymeleaf.isEmptyAssignmentValue("'   '")).isTrue();
    assertThat(Thymeleaf.isEmptyAssignmentValue("\"  \"")).isTrue();
  }

  @Test
  void isEmptyAssignmentValue_nonEmptyValues() {
    assertThat(Thymeleaf.isEmptyAssignmentValue("'foo'")).isFalse();
    assertThat(Thymeleaf.isEmptyAssignmentValue("#{logo}")).isFalse();
    assertThat(Thymeleaf.isEmptyAssignmentValue("@{/logo.png}")).isFalse();
    assertThat(Thymeleaf.isEmptyAssignmentValue("foo")).isFalse();
  }

  private static TagNode tagWithThAttr(String thAttrValue) {
    TagNode node = new TagNode();
    node.getAttributes().add(new Attribute("th:attr", thAttrValue));
    return node;
  }

  private static Stream<Arguments> thAttrValuesWithEmbeddedCommas() {
    return Stream.of(
      Arguments.of(
        "alt=#{messages.greeting(user.name, 'guest')}, title=#{t}",
        "#{messages.greeting(user.name, 'guest')}"),
      Arguments.of("alt='Hello, world', title=#{t}", "'Hello, world'"),
      Arguments.of("alt=${items[0, 1]}, title=#{t}", "${items[0, 1]}"),
      Arguments.of("alt='it\\'s, fine', title=#{t}", "'it\\'s, fine'"));
  }

  private static TagNode tagWithoutThAttr() {
    return new TagNode();
  }
}
