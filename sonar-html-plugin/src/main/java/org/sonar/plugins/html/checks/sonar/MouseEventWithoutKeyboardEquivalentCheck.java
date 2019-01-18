/*
 * SonarHTML :: SonarQube Plugin
 * Copyright (c) 2010-2019 SonarSource SA and Matthijs Galesloot
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

import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "MouseEventWithoutKeyboardEquivalentCheck")
public class MouseEventWithoutKeyboardEquivalentCheck extends AbstractPageCheck {

  @Override
  public void startElement(TagNode node) {
    if (node.getLocalName().equals(node.getNodeName())) {
      String attribute = null;

      if (hasOnClick(node) && !hasOnKeyPress(node)) {
        attribute = "onKeyPress";
      } else if (hasOnMouseover(node) && !hasOnFocus(node)) {
        attribute = "onFocus";
      } else if (hasOnMouseout(node) && !hasOnBlur(node)) {
        attribute = "onBlur";
      }

      if (attribute != null) {
        createViolation(node.getStartLinePosition(), "Add a '" + attribute + "' attribute to this <" + node.getNodeName() + "> tag.");
      }
    }
  }

  private static boolean hasOnClick(TagNode node) {
    return hasEventHandlerAttribute(node, "CLICK");
  }

  private static boolean hasOnKeyPress(TagNode node) {
    return hasEventHandlerAttribute(node, "KEYPRESS");
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

}
