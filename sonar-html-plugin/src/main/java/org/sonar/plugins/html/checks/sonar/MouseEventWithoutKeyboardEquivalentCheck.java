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

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.html.api.Helpers;
import org.sonar.plugins.html.api.HtmlConstants;
import org.sonar.plugins.html.api.accessibility.Aria;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "MouseEventWithoutKeyboardEquivalentCheck")
public class MouseEventWithoutKeyboardEquivalentCheck extends AbstractPageCheck {

  private static final String DEFAULT_WHITELISTED_ELEMENTS = "";

  // Angular pseudo-events and Vue modifiers. Key names are limited to 10 characters and realistic
  // combinations to five modifiers so malformed bindings are not mistaken for event handlers.
  private static final String EVENT_MODIFIERS = "(?:\\.[\\w-]{1,10}){0,5}";
  private static final Map<String, Pattern> EVENT_PATTERNS = new ConcurrentHashMap<>();
  private static final Set<String> NATIVELY_ACTIVATABLE_ROLES = Set.of("textbox", "checkbox", "radio", "listbox");

  @RuleProperty(
    key = "whitelistedElements",
    description = "Comma-separated list of native HTML elements to ignore for all mouse-event keyboard-equivalence checks.",
    defaultValue = DEFAULT_WHITELISTED_ELEMENTS)
  public String whitelistedElements = DEFAULT_WHITELISTED_ELEMENTS;

  private Set<String> whitelistedElementsSet = Set.of();

  @Override
  public void startDocument(List<Node> nodes) {
    whitelistedElementsSet = parseWhitelistedElements(whitelistedElements);
  }

  @Override
  public void startElement(TagNode node) {
    if (HtmlConstants.hasKnownHTMLTag(node)) {
      String attribute = null;

      if (isException(node)) {
        return;
      }

      var roleAttributeValue = node.getAttribute("role");
      if (roleAttributeValue != null && Helpers.isDynamicValue(roleAttributeValue, getHtmlSourceCode())) {
        return;
      }
      String[] roles = new String[]{};

      if (roleAttributeValue != null) {
        roles = roleAttributeValue.split(" ");
      } else {
        var role = Aria.getImplicitRole(node);

        if (role != null) {
          roles = new String[]{
            role.toString()
          };

          // Explicit ARIA roles still require keyboard handlers; only native controls provide them.
          if (NATIVELY_ACTIVATABLE_ROLES.contains(role.toString())) {
            return;
          }
        }
      }

      if (Arrays.stream(roles).anyMatch(MouseEventWithoutKeyboardEquivalentCheck::isAnInteractiveRole)) {
        return;
      }

      if ((hasOnClick(node) || hasButtonRole(node)) && !(hasOnKeyPress(node) || hasOnKeyDown(node) || hasOnKeyUp(node))) {
        attribute = "onKeyDown|onKeyUp";
      } else if (hasOnMouseover(node) && !hasOnFocus(node)) {
        attribute = "onFocus";
      } else if (hasOnMouseout(node) && !hasOnBlur(node)) {
        attribute = "onBlur";
      }

      if (attribute != null) {
        createViolation(node, "Add a '" + attribute + "' attribute to this <" + node.getNodeName() + "> tag.");
      }
    }
  }

  private static boolean isAnInteractiveRole(String role) {
    return "textbox".equalsIgnoreCase(role);
  }

  private boolean isException(TagNode node) {
    return isClickableButtonLikeElement(node) || ((isInput(node) || isButton(node) || isHyperlink(node) || isSummary(node)) && hasOnClick(node) && !hasButtonRole(node));
  }

  private static boolean hasOnClick(TagNode node) {
    return hasEventHandlerAttribute(node, "CLICK");
  }

  private static boolean hasOnKeyPress(TagNode node) {
    return hasEventHandlerAttribute(node, "KEYPRESS");
  }

  private static boolean hasOnKeyDown(TagNode node) {
    return hasEventHandlerAttribute(node, "KEYDOWN");
  }

  private static boolean hasOnKeyUp(TagNode node) {
    return hasEventHandlerAttribute(node, "KEYUP");
  }

  private static boolean hasOnMouseover(TagNode node) {
    return hasEventHandlerAttribute(node, "MOUSEOVER");
  }

  private static boolean hasOnFocus(TagNode node) {
    return hasEventHandlerAttribute(node, "FOCUS");
  }

  private static boolean hasOnMouseout(TagNode node) {
    // Angular 1 only has a 'NG-MOUSELEAVE' attribute, no 'NG-MOUSEOUT'
    return hasEventHandlerAttribute(node, "MOUSEOUT") || hasAttribute(node, "NG-MOUSELEAVE");
  }

  private static boolean hasOnBlur(TagNode node) {
    return hasEventHandlerAttribute(node, "BLUR");
  }

  private static boolean hasEventHandlerAttribute(TagNode node, String eventName) {
    return hasAttribute(node, "ON" + eventName)
      // Angular event binding attributes
      || hasAttribute(node, "ON-" + eventName)
      || hasAttribute(node, "NG-" + eventName)
      || hasEventBinding(node, eventName);
  }

  private static boolean hasEventBinding(TagNode node, String eventName) {
    Pattern pattern = EVENT_PATTERNS.computeIfAbsent(eventName, name -> Pattern.compile(
      "\\(" + name + EVENT_MODIFIERS + "\\)"
        + "|(?:v-on:)?" + name + EVENT_MODIFIERS,
      Pattern.CASE_INSENSITIVE));
    return node.getAttributes().stream().anyMatch(attribute -> pattern.matcher(attribute.getName()).matches());
  }

  private static boolean hasAttribute(TagNode node, String attributeName) {
    return node.getAttribute(attributeName) != null;
  }

  private static boolean hasButtonRole(TagNode node) {
    return "BUTTON".equalsIgnoreCase(node.getPropertyValue("role"));
  }

  private static boolean isInput(TagNode node) {
    return "INPUT".equalsIgnoreCase(node.getNodeName()) &&
        ("BUTTON".equalsIgnoreCase(node.getPropertyValue("type")) || "SUBMIT".equalsIgnoreCase(node.getPropertyValue("type")));
  }

  private static boolean isButton(TagNode node) {
    return "BUTTON".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isHyperlink(TagNode node) {
    return "A".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isSummary(TagNode node) {
    return "SUMMARY".equalsIgnoreCase(node.getNodeName());
  }

  private boolean isClickableButtonLikeElement(TagNode node) {
    var nodeName = node.getNodeName();
    if (nodeName == null) {
      return false;
    }
    var normalizedNodeName = nodeName.toUpperCase(Locale.ROOT);
    return whitelistedElementsSet.contains(normalizedNodeName);
  }

  private static Set<String> parseWhitelistedElements(String whitelistedElements) {
    if (whitelistedElements == null || whitelistedElements.isBlank()) {
      return Set.of();
    }
    return Arrays.stream(whitelistedElements.split(","))
      .map(String::trim)
      .filter(element -> !element.isEmpty())
      .map(element -> element.toUpperCase(Locale.ROOT))
      .collect(Collectors.toSet());
  }

}
