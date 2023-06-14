/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (c) 2010-2023 SonarSource SA and Matthijs Galesloot
 * sonarqube@googlegroups.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sonar.plugins.html.checks.sonar;

import java.util.regex.Pattern;
import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "MouseEventWithoutKeyboardEquivalentCheck")
public class MouseEventWithoutKeyboardEquivalentCheck extends AbstractPageCheck {

  // Angular 2+ allows key names for the onKeydown pseudo-event to prevent checking the key name manually
  // This pseudo-event also allows key combinations
  // Key names are limited to 10 charters and the combination of keys is realistic limited to 5
  private static final Pattern KEY_DOWN_WITH_KEY_NAME = Pattern.compile("\\(keydown(\\.\\w{1,10}){1,5}\\)");

  @Override
  public void startElement(TagNode node) {
    if (node.getLocalName().equals(node.getNodeName())) {
      String attribute = null;

      if (isException(node)) {
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

  private static boolean isException(TagNode node) {
    return isClickableLightningButton(node) || ((isInput(node) || isButton(node) || isHyperlink(node)) && hasOnClick(node) && !hasButtonRole(node));
  }

  private static boolean hasOnClick(TagNode node) {
    return hasEventHandlerAttribute(node, "CLICK");
  }

  private static boolean hasOnKeyPress(TagNode node) {
    return hasEventHandlerAttribute(node, "KEYPRESS");
  }

  private static boolean hasOnKeyDown(TagNode node) {
    return hasEventHandlerAttribute(node, "KEYDOWN") || hasAngularKeyDownWithKeyName(node);
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
      // angular event binding attributes
      || hasAttribute(node, "(" + eventName + ")")
      || hasAttribute(node, "ON-" + eventName)
      || hasAttribute(node, "NG-" + eventName);
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

  private static boolean isClickableLightningButton(TagNode node) {
    return "LIGHTNING-BUTTON".equalsIgnoreCase(node.getNodeName())
      || "LIGHTNING-BUTTON-ICON".equalsIgnoreCase(node.getNodeName())
      || "LIGHTNING-BUTTON-MENU".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean hasAngularKeyDownWithKeyName(TagNode node) {
    return node.getAttributes().stream().anyMatch(a -> KEY_DOWN_WITH_KEY_NAME.matcher(a.getName()).find());
  }
}
