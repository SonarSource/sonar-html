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
import org.junit.jupiter.params.provider.ValueSource;
import org.sonar.plugins.html.node.Attribute;
import org.sonar.plugins.html.node.TagNode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.sonar.plugins.html.api.accessibility.AccessibilityUtils.getTemplateTextValue;
import static org.sonar.plugins.html.api.accessibility.AccessibilityUtils.unwrapStaticStringLiteral;

class AccessibilityUtilsTest {

  @ParameterizedTest
  @MethodSource("staticStringLiterals")
  void unwrapsStaticStringLiterals(String value, String expected) {
    assertThat(unwrapStaticStringLiteral(value)).isEqualTo(expected);
  }

  private static Stream<Arguments> staticStringLiterals() {
    return Stream.of(
      Arguments.of("'image of a sunrise'", "image of a sunrise"),
      Arguments.of("\"image of a sunrise\"", "image of a sunrise"),
      Arguments.of("`image of a sunrise`", "image of a sunrise"),
      // whitespace around the whole expression is not part of the value
      Arguments.of("  'a sunrise'  ", "a sunrise"),
      // an escaped quote does not terminate the literal, and escapes survive unwrapping
      Arguments.of("'it\\'s a sunrise'", "it\\'s a sunrise"),
      Arguments.of("\"say \\\"hi\\\"\"", "say \\\"hi\\\""),
      // the literal ends on an escaped backslash, not on an escaped quote
      Arguments.of("'a path C:\\\\'", "a path C:\\\\"),
      // the opposite quote character is ordinary text
      Arguments.of("'say \"hi\"'", "say \"hi\""),
      // a template literal without interpolation is still static
      Arguments.of("`say 'hi'`", "say 'hi'"));
  }

  @Test
  void unwrapsEmptyLiteral() {
    assertThat(unwrapStaticStringLiteral("''")).isEmpty();
    assertThat(unwrapStaticStringLiteral("\"\"")).isEmpty();
  }

  @Test
  void keepsWhitespaceInsideTheLiteral() {
    assertThat(unwrapStaticStringLiteral("' a sunrise '")).isEqualTo(" a sunrise ");
  }

  @ParameterizedTest
  @ValueSource(strings = {
    // identifiers, property accesses and calls
    "data.imageAlt",
    "IMAGE_ALT",
    "imageSrc()",
    "blok().image?.alt ?? ''",
    "flavour.image_alt || flavour.name",
    // concatenations that merely start and end with a matching quote
    "'a' + imageVar + 'b'",
    "'image' + suffix + 'text'",
    "'a' + 'b'",
    "'a''b'",
    "('a') + ('b')",
    // parentheses are deliberately not unwrapped, so even a parenthesized literal stays unresolved
    "('a sunrise')",
    "(( 'a sunrise' ))",
    // a literal that is only part of the expression
    "'a photo'.toUpperCase()",
    "translate('a photo')",
    "condition ? 'a photo' : 'a picture'",
    // interpolated template literals are resolved at runtime
    "`photo of ${name}`",
    // unterminated literals
    "'a sunrise",
    "'a sunrise\\",
    "'",
    "`photo of $",
    // no literal at all
    "",
    "   ",
  })
  void returnsNullForExpressionsThatAreNotStaticLiterals(String value) {
    assertThat(unwrapStaticStringLiteral(value)).isNull();
  }

  @Test
  void returnsNullForNull() {
    assertThat(unwrapStaticStringLiteral(null)).isNull();
  }

  @ParameterizedTest
  @MethodSource("renderedTextBindings")
  void returnsTheValueOfBindingsThatRenderText(String attributeName, String attributeValue) {
    assertThat(getTemplateTextValue(tag(attributeName, attributeValue))).isEqualTo(attributeValue);
  }

  private static Stream<Arguments> renderedTextBindings() {
    return Stream.of(
      // template-text attributes
      Arguments.of("th:text", "#{title}"),
      Arguments.of("th:utext", "#{title}"),
      Arguments.of("v-text", "title"),
      Arguments.of("v-html", "markup"),
      // Angular property bindings
      Arguments.of("[innerHTML]", "markup"),
      Arguments.of("[innerText]", "title"),
      Arguments.of("[textContent]", "title"),
      // Vue property bindings, long and shorthand
      Arguments.of("v-bind:innerHTML", "markup"),
      Arguments.of("v-bind:innerText", "title"),
      Arguments.of(":innerHTML", "markup"),
      Arguments.of(":textContent", "title"),
      // attribute names are matched case-insensitively
      Arguments.of("[INNERHTML]", "markup"),
      Arguments.of(":InnerText", "title"));
  }

  @ParameterizedTest
  @MethodSource("bindingsThatRenderNothing")
  void returnsNullForBindingsThatRenderNothing(String attributeName, String attributeValue) {
    assertThat(getTemplateTextValue(tag(attributeName, attributeValue))).isNull();
  }

  private static Stream<Arguments> bindingsThatRenderNothing() {
    return Stream.of(
      // a literal attribute is not a binding, and there is no innerHTML HTML attribute
      Arguments.of("innerHTML", "not a binding"),
      // [attr.x] writes an HTML attribute named x, not the DOM property
      Arguments.of("[attr.innerHTML]", "markup"),
      Arguments.of("attr.innerHTML", "markup"),
      Arguments.of("[attr.innerText]", "title"),
      Arguments.of("[attr.textContent]", "title"),
      // Vue's dynamic argument binds the property named by the runtime value of innerHTML
      Arguments.of(":[innerHTML]", "markup"),
      // an unrelated binding provides no text content
      Arguments.of("[title]", "title"),
      // a binding with no usable value renders nothing
      Arguments.of("[innerHTML]", ""),
      Arguments.of("[innerHTML]", "''"),
      Arguments.of("th:text", "'  '"));
  }

  @Test
  void anEmptyBindingDoesNotHideANonEmptyOne() {
    TagNode node = tag("[innerHTML]", "");
    node.getAttributes().add(new Attribute(":innerText", "title"));

    assertThat(getTemplateTextValue(node)).isEqualTo("title");
  }

  @Test
  void returnsNullWhenNoAttributeRendersText() {
    assertThat(getTemplateTextValue(new TagNode())).isNull();
  }

  private static TagNode tag(String attributeName, String attributeValue) {
    TagNode node = new TagNode();
    node.getAttributes().add(new Attribute(attributeName, attributeValue));
    return node;
  }
}
