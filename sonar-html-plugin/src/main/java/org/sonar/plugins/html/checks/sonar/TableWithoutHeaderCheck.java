/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (c) 2010-2022 SonarSource SA and Matthijs Galesloot
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
import org.sonar.plugins.html.node.Attribute;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "S5256")
public class TableWithoutHeaderCheck extends AbstractPageCheck {

  @Override
  public void startElement(TagNode node) {
    if (isTable(node) && !isLayout(node) && !isHidden(node) && !hasHeader(node) && !hasThymeleafTemplate(node)) {
      createViolation(node, "Add \"<th>\" headers to this \"<table>\".");
    }
  }

  private static boolean isTable(TagNode node) {
    return "TABLE".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isLayout(TagNode node) {
    String role = node.getAttribute("role");
    return "PRESENTATION".equalsIgnoreCase(role) || "NONE".equalsIgnoreCase(role);
  }

  private static boolean isHidden(TagNode node) {
    String ariaHidden = node.getAttribute("aria-hidden");
    return "TRUE".equalsIgnoreCase(ariaHidden);
  }

  private static boolean hasThymeleafTemplate(TagNode node) {
    return node.getAttributes().stream().anyMatch(TableWithoutHeaderCheck::isAttributeThymeleafTemplated)
      || node.getChildren().stream().map(TagNode::getAttributes).anyMatch(attributes -> attributes.stream().anyMatch(TableWithoutHeaderCheck::isAttributeThymeleafTemplated));
  }

  private static boolean isAttributeThymeleafTemplated(Attribute attribute) {
    String attributeName = attribute.getName();
    return attributeName.startsWith("th:replace") || attributeName.startsWith("th:insert");
  }

  private static boolean hasHeader(TagNode node) {
    return node.getChildren().stream().anyMatch(TableWithoutHeaderCheck::isTableHeader) ||
      node.getChildren().stream().filter(child -> !isTable(child)).anyMatch(TableWithoutHeaderCheck::hasHeader);
  }

  private static boolean isTableHeader(TagNode node) {
    return "TH".equalsIgnoreCase(node.getNodeName());
  }
}
