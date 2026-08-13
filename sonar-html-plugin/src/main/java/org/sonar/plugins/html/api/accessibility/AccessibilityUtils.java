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

import java.util.List;
import java.util.Set;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.sonar.plugins.html.api.Thymeleaf;
import org.sonar.plugins.html.node.Attribute;
import org.sonar.plugins.html.node.TagNode;

import static org.sonar.plugins.html.api.HtmlConstants.isInteractiveElement;

public class AccessibilityUtils {

  /**
   * Attributes that template engines use to inject text content into an element at render time:
   * Thymeleaf {@code th:text}/{@code th:utext} and Vue {@code v-text}/{@code v-html}. Centralized
   * here so accessibility checks that ask "does this element get its text from a template?" all
   * see the same definition.
   */
  public static final Set<String> TEMPLATE_TEXT_ATTRIBUTES = Set.of("th:text", "th:utext", "v-text", "v-html");

  /**
   * DOM properties a framework binds to write an element's text content at render time. Unlike
   * {@link #TEMPLATE_TEXT_ATTRIBUTES} these are property names, not attribute names, matched only
   * in the binding forms that reliably write the DOM property — see
   * {@link #getReliablyBoundValue(TagNode, String)}.
   */
  public static final Set<String> TEXT_CONTENT_PROPERTIES = Set.of("innerHTML", "innerText", "textContent");

  private AccessibilityUtils() {
    // utility class
  }

  /**
   * Returns the expression {@code element} renders its text content from — a template-text attribute
   * or a bound text-content property — or {@code null} when it renders none.
   */
  @CheckForNull
  public static String getTemplateTextValue(TagNode element) {
    for (String attributeName : TEMPLATE_TEXT_ATTRIBUTES) {
      String value = element.getAttribute(attributeName);
      if (!Thymeleaf.isEmptyValue(value)) {
        return value;
      }
    }

    for (String propertyName : TEXT_CONTENT_PROPERTIES) {
      String value = getReliablyBoundValue(element, propertyName);
      if (!Thymeleaf.isEmptyValue(value)) {
        return value;
      }
    }

    return null;
  }

  /**
   * Returns the value bound to {@code propertyName} through a spelling that reliably writes the
   * DOM property — Angular {@code [x]}, Vue {@code v-bind:x}/{@code :x} — or {@code null}
   * otherwise. Unlike {@link TagNode#getProperty(String)}, this deliberately excludes:
   * <ul>
   *   <li>Angular {@code [attr.x]}/{@code attr.x}: these write an HTML <em>attribute</em> named
   *   {@code x}, not the DOM property. There is no standard {@code innerHTML}/{@code innerText}/
   *   {@code textContent} HTML attribute, so the browser renders nothing.</li>
   *   <li>Vue's dynamic argument {@code :[x]}: the bound property name comes from the runtime
   *   value of {@code x}, not from {@code x} itself, so it may not target {@code propertyName} at all.</li>
   * </ul>
   */
  @CheckForNull
  private static String getReliablyBoundValue(TagNode element, String propertyName) {
    for (String bindingSpelling : List.of("[" + propertyName + "]", "v-bind:" + propertyName, ":" + propertyName)) {
      String value = element.getAttribute(bindingSpelling);
      if (value != null) {
        return value;
      }
    }
    return null;
  }

  /**
   * Returns whether {@code element} carries any template-text attribute or bound text-content
   * property with a usable value.
   */
  public static boolean hasNonEmptyTemplateTextAttribute(TagNode element) {
    return getTemplateTextValue(element) != null;
  }

  public static boolean isHiddenFromScreenReader(TagNode element) {
    return (
      (
        "input".equalsIgnoreCase(element.getNodeName()) &&
          "hidden".equalsIgnoreCase(element.getPropertyValue("type"))
      ) ||
        "true".equalsIgnoreCase(element.getPropertyValue("aria-hidden"))
    );
  }

  public static boolean isDisabledElement(TagNode element) {
    var disabledAttr = element.getAttribute("disabled");
    if (disabledAttr != null) {
      return true;
    }

    var ariaDisabledAttr = element.getAttribute("aria-disabled");
    return "true".equalsIgnoreCase(ariaDisabledAttr);
  }

  /**
   * Unwraps a binding expression that is a single static string literal, e.g.
   * {@code "'image of a sunrise'"} to {@code "image of a sunrise"}, keeping escape sequences as
   * written. Returns {@code null} when the value is not statically resolvable: identifiers,
   * concatenations, calls, ternaries and interpolated template literals. Parenthesized literals
   * ({@code ('a sunrise')}) are deliberately left unresolved — templates do not write them, and
   * missing one costs a false negative, never a false positive.
   */
  @CheckForNull
  public static String unwrapStaticStringLiteral(@Nullable String value) {
    if (value == null) {
      return null;
    }

    String expression = value.trim();
    if (expression.length() < 2) {
      return null;
    }

    char quote = expression.charAt(0);
    if (!isQuote(quote)) {
      return null;
    }

    int index = 1;
    while (index < expression.length()) {
      char currentCharacter = expression.charAt(index);
      if (currentCharacter == '\\') {
        // Skip the escaped character; running past the end leaves the literal unterminated.
        index += 2;
      } else if (quote == '`' && isInterpolationStart(expression, index)) {
        return null;
      } else if (currentCharacter == quote) {
        // Anything after the closing quote — an operator, a member access, a second literal —
        // means the expression is more than this one literal.
        return index == expression.length() - 1 ? expression.substring(1, index) : null;
      } else {
        index++;
      }
    }

    // The opening quote is never closed.
    return null;
  }

  private static boolean isInterpolationStart(String expression, int index) {
    return expression.charAt(index) == '$'
      && index + 1 < expression.length()
      && expression.charAt(index + 1) == '{';
  }

  private static boolean isQuote(char character) {
    return character == '\'' || character == '"' || character == '`';
  }

  /**
   * Returns whether {@code attribute} was matched under a framework binding form
   * (Angular {@code [x]}/{@code [attr.x]}, Vue {@code :x}/{@code v-bind:x}/{@code :[x]})
   * rather than under its plain {@code canonicalName}.
   */
  public static boolean isBindingForm(Attribute attribute, String canonicalName) {
    return !canonicalName.equalsIgnoreCase(attribute.getName());
  }

  public static boolean isFocusableElement(TagNode element) {
    String tabindex = element.getPropertyValue("tabindex");
    try {
      if (isInteractiveElement(element)) {
        return tabindex == null || Double.parseDouble(tabindex) >= 0;
      }
      return tabindex != null && Double.parseDouble(tabindex) >= 0;
    } catch (NumberFormatException e) {
      // if it's declaratively set (i.e., angular or php), we assume it can be positive
      return true;
    }
  }
}
