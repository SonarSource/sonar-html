/*
 * SonarQube HTML
 * Copyright (C) 2010-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
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
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.html.api.Helpers;
import org.sonar.plugins.html.api.accessibility.Aria;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "MouseEventWithoutKeyboardEquivalentCheck")
public class MouseEventWithoutKeyboardEquivalentCheck extends AbstractPageCheck {

  private static final String DEFAULT_WHITELISTED_ELEMENTS = "lightning-button,lightning-button-icon,lightning-button-menu";

  // Angular 2+ allows key names for the onKeydown pseudo-event to prevent checking the key name manually
  // This pseudo-event also allows key combinations
  // Key names are limited to 10 charters and the combination of keys is realistic limited to 5
  // Vue also supports key modifiers on @keydown (e.g. @keydown.enter, v-on:keydown.enter)
  // Note: Vue's @keydown shorthand has the @ stripped by the parser, leaving bare "keydown.enter" as the attribute name
  private static final Pattern KEY_DOWN_WITH_KEY_NAME = Pattern.compile(
    "\\(keydown(\\.\\w{1,10}){1,5}\\)" +
    "|keydown(\\.\\w{1,10}){1,5}" +
    "|v-on:keydown(\\.\\w{1,10}){1,5}",
    Pattern.CASE_INSENSITIVE);

  // Angular 2+ allows key names for the onKeyup pseudo-event, similar to keydown
  // Vue also supports key modifiers on @keyup (e.g. @keyup.enter, v-on:keyup.enter)
  // Note: Vue's @keyup shorthand has the @ stripped by the parser, leaving bare "keyup.enter" as the attribute name
  private static final Pattern KEY_UP_WITH_KEY_NAME = Pattern.compile(
    "\\(keyup(\\.\\w{1,10}){1,5}\\)" +
    "|keyup(\\.\\w{1,10}){1,5}" +
    "|v-on:keyup(\\.\\w{1,10}){1,5}",
    Pattern.CASE_INSENSITIVE);

  @RuleProperty(
    key = "whitelistedElements",
    description = "Comma-separated list of custom elements to ignore when they expose an onClick attribute without keyboard event handlers.",
    defaultValue = DEFAULT_WHITELISTED_ELEMENTS)
  public String whitelistedElements = DEFAULT_WHITELISTED_ELEMENTS;

  private Set<String> whitelistedElementsSet = Set.of();

  @Override
  public void startDocument(List<Node> nodes) {
    whitelistedElementsSet = parseWhitelistedElements(whitelistedElements);
  }

  @Override
  public void startElement(TagNode node) {
    if (node.getLocalName().equals(node.getNodeName())) {
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
        }
      }

      if (Arrays.stream(roles).anyMatch(MouseEventWithoutKeyboardEquivalentCheck::isAnInteractiveRole)) {
        return;
      }

      if ((hasOnClick(node) || hasButtonRole(node)) && !(hasOnKeyPress(node) || hasOnKeyDown(node) || hasOnKeyUp(node))) {
        attribute = "onKeyPress|onKeyDown|onKeyUp";
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
    // Vue's @keydown shorthand has @ stripped by the parser, leaving bare "keydown" attribute name
    return hasEventHandlerAttribute(node, "KEYDOWN") || hasAttribute(node, "KEYDOWN") || hasKeyDownWithKeyName(node);
  }

  private static boolean hasOnKeyUp(TagNode node) {
    // Vue's @keyup shorthand has @ stripped by the parser, leaving bare "keyup" attribute name
    return hasEventHandlerAttribute(node, "KEYUP") || hasAttribute(node, "KEYUP") || hasKeyUpWithKeyName(node);
  }

  private static boolean hasOnMouseover(TagNode node) {
    return hasEventHandlerAttribute(node, "MOUSEOVER");
  }

  private static boolean hasOnFocus(TagNode node) {
    return hasEventHandlerAttribute(node, "FOCUS");
  }

  private static boolean hasOnMouseout(TagNode node) {
    return hasAttribute(node, "ONMOUSEOUT")
      || hasAttribute(node, "(MOUSEOUT)")
      || hasAttribute(node, "ON-MOUSEOUT")
      // Angular 1 only has a 'NG-MOUSELEAVE' attribute, no 'NG-MOUSEOUT'
      || hasAttribute(node, "NG-MOUSELEAVE");
  }

  private static boolean hasOnBlur(TagNode node) {
    return hasEventHandlerAttribute(node, "BLUR");
  }

  private static boolean hasEventHandlerAttribute(TagNode node, String eventName) {
    return hasAttribute(node, "ON" + eventName)
      // Angular event binding attributes
      || hasAttribute(node, "(" + eventName + ")")
      || hasAttribute(node, "ON-" + eventName)
      || hasAttribute(node, "NG-" + eventName)
      // Vue long form: v-on:eventname (shorthand @eventname has @ stripped by the parser)
      || hasAttribute(node, "V-ON:" + eventName);
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

  private static boolean hasKeyDownWithKeyName(TagNode node) {
    return node.getAttributes().stream().anyMatch(a -> KEY_DOWN_WITH_KEY_NAME.matcher(a.getName()).matches());
  }

  private static boolean hasKeyUpWithKeyName(TagNode node) {
    return node.getAttributes().stream().anyMatch(a -> KEY_UP_WITH_KEY_NAME.matcher(a.getName()).matches());
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
