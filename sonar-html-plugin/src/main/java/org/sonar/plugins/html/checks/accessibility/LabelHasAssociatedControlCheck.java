/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (c) 2010-2024 SonarSource SA and Matthijs Galesloot
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
package org.sonar.plugins.html.checks.accessibility;

import java.util.Arrays;
import java.util.List;
import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.DirectiveNode;
import org.sonar.plugins.html.node.ExpressionNode;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;
import org.sonar.plugins.html.node.TextNode;

@Rule(key = "S6853")
public class LabelHasAssociatedControlCheck extends AbstractPageCheck {
  private static final String MESSAGE = "A form label must be associated with a control.";
  private static final String[] CONTROL_TAGS = {"input", "meter", "output", "progress", "select", "textarea"};
  private boolean foundControl;
  private boolean foundAccessibleLabel;
  private TagNode label;

  @Override
  public void startDocument(List<Node> nodes) {
    label = null;
  }

  @Override
  public void startElement(TagNode node) {
    if (isLabel(node)) {
      label = node;
      if (hasForAttribute(label)) {
        foundControl = true;
      } else {
        foundControl = false;
      }
    } else if (isControl(node)) {
      foundControl = true;
    }
    if (hasAccessibleLabel(node)) {
      foundAccessibleLabel = true;
    }
  }

  private static boolean hasForAttribute(TagNode label) {
    return label.hasProperty("for") || label.hasProperty("htmlFor");
  }

  private static boolean hasAccessibleLabel(TagNode node) {
    return
      node.hasProperty("alt") ||
      node.hasProperty("aria-labelledby") ||
      node.hasProperty("aria-label") ||
      // see https://sonarsource.github.io/rspec/#/rspec/S1926
      "FMT:MESSAGE".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isLabel(TagNode node) {
    return "LABEL".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isControl(TagNode node) {
    return Arrays.stream(CONTROL_TAGS).anyMatch(node.getNodeName()::equalsIgnoreCase);
  }

  @Override
  public void characters(TextNode textNode) {
    if (!textNode.isBlank() && label != null) {
      foundAccessibleLabel = true;
    }
  }

  @Override
  public void directive(DirectiveNode node) {
    if (label != null) {
      foundAccessibleLabel = "?php".equalsIgnoreCase(node.getNodeName());
    }
  }

  @Override
  public void expression(ExpressionNode node) {
    // for JSP
    if (label != null) {
      foundAccessibleLabel = true;
    }
  }

  @Override
  public void endElement(TagNode node) {
    if (isLabel(node)) {
      if ((!foundAccessibleLabel || !foundControl) && label != null) {
        createViolation(label, MESSAGE);
      }
      foundControl = false;
      foundAccessibleLabel = false;
      label = null;
    }
  }
}
