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
package org.sonar.plugins.html.checks.sonar;

import org.sonar.check.Rule;
import org.sonar.plugins.html.api.Helpers;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Attribute;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;
import org.sonar.plugins.html.node.TextNode;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Rule(key = "S5254")
public class LangAttributeCheck extends AbstractPageCheck {
  // hasInvalidLangAncestor: true iff the closest lang-bearing ancestor declares an invalid value.
  // "No lang declared anywhere in the chain" is NOT a descendant violation — that's the page-level
  // concern handled separately via pendingHtmlMissingLang.
  public record TagNodeFlag(@Nullable TagNode tagNode, boolean hasInvalidLangAncestor) {
  }

  private final Deque<TagNodeFlag> langStack = new ArrayDeque<>();
  private boolean inHtmlScope;
  @Nullable
  private TagNode pendingHtmlMissingLang;

  @Override
  public void startDocument(List<Node> nodes) {
    reset();
  }

  @Override
  public void endDocument() {
    // Document ends with the deferred decision still pending (no </html>, no <body>).
    flushPendingHtmlMissingLang();
  }

  private void reset() {
    langStack.clear();
    langStack.push(new TagNodeFlag(null, false));
    inHtmlScope = false;
    pendingHtmlMissingLang = null;
  }

  private void flushPendingHtmlMissingLang() {
    if (pendingHtmlMissingLang != null) {
      createViolation(pendingHtmlMissingLang, HTML_OR_BODY_MISSING_LANG_MESSAGE);
      pendingHtmlMissingLang = null;
    }
  }

  private static final Set<String> ISO_LANGUAGES_SET = Arrays.stream(Locale.getISOLanguages()).collect(Collectors.toSet());
  public static final String DEFAULT_MESSAGE = "Text is missing a valid lang attribute in its ancestor elements";
  public static final String HTML_OR_BODY_MISSING_LANG_MESSAGE = "Add \"lang\" and/or \"xml:lang\" attributes to the \"<html>\" or \"<body>\" element";

  @Override
  public void startElement(TagNode node) {
    if (isHtmlTag(node)) {
      // A new <html> start before the previous one was resolved (back-to-back or nested):
      // surface the deferred violation rather than dropping it, then start a fresh decision.
      flushPendingHtmlMissingLang();
      reset();
      inHtmlScope = true;
      if (!hasLangAttribute(node)) {
        // Defer the page-level decision: <body lang> may still rescue it.
        pendingHtmlMissingLang = node;
      }
    } else if (isBodyTag(node) && pendingHtmlMissingLang != null && hasLangAttribute(node)) {
      // <body> declares lang (whether the value is valid or not) — the page-level "missing lang"
      // check passes. An invalid value on body still triggers descendant violations below.
      pendingHtmlMissingLang = null;
    }
    if (!inHtmlScope) {
      return;
    }

    boolean hasInvalidLangAncestor = hasLangAttribute(node)
      ? !ownLangIsValid(node)
      : langStack.getLast().hasInvalidLangAncestor();
    langStack.addLast(new TagNodeFlag(node, hasInvalidLangAncestor));
    if (hasInvalidLangAncestor && hasTextInAttributesToValidate(node)) {
      createViolation(node, DEFAULT_MESSAGE);
    }
  }

  @Override
  public void endElement(TagNode node) {
    if (isHtmlTag(node)) {
      // </html> reached without any <body> resolving the deferred check.
      flushPendingHtmlMissingLang();
    }
    if (!inHtmlScope) {
      return;
    }
    var lastNode = langStack.getLast().tagNode();
    if (lastNode != null && lastNode.getNodeName().equals(node.getNodeName())) {
      langStack.removeLast();
    }
    if (isHtmlTag(node)) {
      inHtmlScope = false;
    }
  }

  @Override
  public void characters(TextNode textNode) {
    if (!inHtmlScope) {
      return;
    }
    if (textNode.getCode().isBlank() || Helpers.isDynamicValue(textNode.getCode().trim(), getHtmlSourceCode())) {
      return;
    }
    if (langStack.getLast().hasInvalidLangAncestor()) {
      createViolation(textNode, DEFAULT_MESSAGE);
    }
  }

  private boolean ownLangIsValid(TagNode node) {
    String value = getLangAttributeValue(node);
    if (value == null) {
      // Dynamic/programmatic lang (Thymeleaf, WordPress, dynamic expressions) — assume valid.
      return true;
    }
    return isValidLangAttributeValue(value);
  }

  private static boolean hasTextInAttributesToValidate(TagNode node) {
    String nodeName = node.getNodeName().toLowerCase(Locale.ENGLISH);

    // alt attribute
    if (("img".equals(nodeName) || "area".equals(nodeName) ||
            ("input".equals(nodeName) && "image".equalsIgnoreCase(node.getAttribute("type"))))
            && hasNonEmptyAttr(node, "alt")) {
      return true;
    }

    // aria-label
    if (hasNonEmptyAttr(node, "aria-label")) {
      return true;
    }

    // title attribute
    if (hasNonEmptyAttr(node, "title")) {
      return true;
    }

    // input with value (text-like types)
    if ("input".equals(nodeName)) {
      String type = node.getAttribute("type");
      if (type == null || type.isEmpty() || isTextLikeInput(type)) {
        return hasNonEmptyAttr(node, "value");
      }
    }

    return false;
  }

  private static boolean hasNonEmptyAttr(TagNode node, String attrName) {
    String value = node.getAttribute(attrName);
    return value != null && !value.trim().isEmpty();
  }

  private static boolean isTextLikeInput(String type) {
    String t = type.toLowerCase(Locale.ENGLISH);
    return "text".equals(t) || "search".equals(t) || "email".equals(t) ||
            "tel".equals(t) || "url".equals(t) || "password".equals(t);
  }

  static final Pattern LANG_CODE_PATTERN = Pattern.compile("[a-zA-Z0-9-]+");

  private static boolean isHtmlTag(TagNode node) {
    return "HTML".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isBodyTag(TagNode node) {
    return "BODY".equalsIgnoreCase(node.getNodeName());
  }

  private boolean isValidLangAttributeValue(String langAttributeValue) {
    if (Helpers.isDynamicValue(langAttributeValue, getHtmlSourceCode())) {
      return true;
    }
    // Values containing non-language characters are template placeholders (e.g. %lang%, #{locale})
    if (!langAttributeValue.isEmpty() && !LANG_CODE_PATTERN.matcher(langAttributeValue).matches()) {
      return true;
    }
    var parts = langAttributeValue.split("-");
    if (parts[0].length() != 2) {
      return false;
    }
    return ISO_LANGUAGES_SET.contains(parts[0].toLowerCase(Locale.ENGLISH));
  }

  private static String getLangAttributeValue(TagNode node) {
    var lang = node.getPropertyValue("lang");
    if (lang != null) {
      return lang.trim();
    }
    lang = node.getPropertyValue("xml:lang");
    if (lang != null) {
      return lang.trim();
    }
    return null;
  }

  private boolean hasLangAttribute(TagNode node) {
    return node.hasProperty("lang")
            || node.hasProperty("xml:lang")
            || hasWordPressLangAttribute(node)
            || hasThymeleafLangAttribute(node)
            || hasDynamicLangAttribute(node);
  }

  private boolean hasDynamicLangAttribute(TagNode node) {
    var lang = getLangAttributeValue(node);
    if (lang == null) {
      return false;
    }
    return Helpers.isDynamicValue(lang, getHtmlSourceCode());
  }

  /**
   * Using WordPress, HTML attributes can be set using the php function `language_attributes`
   */
  private static boolean hasWordPressLangAttribute(TagNode node) {
    return node.getAttributes().stream()
            .map(Attribute::getName)
            .anyMatch(attributeName -> attributeName.contains("?php") && attributeName.contains("language_attributes"));
  }

  // Inside th:attr="..." (a comma-separated list of key=value pairs), only `lang=` and `xml:lang=`
  // at a key boundary (start of string or after a comma) actually set the HTML lang. The previous
  // `contains("lang=")` heuristic matched `data-lang=`, `aria-lang=`, etc.
  private static final Pattern THYMELEAF_LANG_ATTR_PATTERN = Pattern.compile("(?:^|,)\\s*(?:xml:)?lang\\s*=");

  /**
   * In Thymeleaf there are multiple ways of specifying the lang attribute:
   * - using the th:lang, th:xmllang, th:lang-xmllang attributes (lang-xmllang would set both xmllang and lang attributes)
   * - using the th:attr attribute for specifying different attributes. Example "th:attr="lang=html,xml:lang=html""
   */
  private static boolean hasThymeleafLangAttribute(TagNode node) {
    String thAttrValue = node.getAttribute("th:attr");
    return node.hasProperty("th:lang")
            || node.hasProperty("th:xmllang")
            || node.hasProperty("th:lang-xmllang")
            || (thAttrValue != null && THYMELEAF_LANG_ATTR_PATTERN.matcher(thAttrValue).find());
  }

}
