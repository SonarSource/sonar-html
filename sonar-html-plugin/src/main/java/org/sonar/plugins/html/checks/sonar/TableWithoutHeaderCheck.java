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

import java.util.List;
import java.util.Set;
import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Attribute;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "S5256")
public class TableWithoutHeaderCheck extends AbstractPageCheck {

  private static final Set<String> THYMELEAF_FRAGMENT_INSERTION_KEYWORDS = Set.of("th:insert", "th:include", "th:replace");

  @Override
  public void startElement(TagNode node) {
    if (isTable(node) && !isLayout(node) && !isHidden(node) && !hasHeader(node) && !hasThymeleafFragmentInsertion(node)) {
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

  private static boolean hasThymeleafFragmentInsertion(TagNode node) {
    return hasThymeleafFragmentInsertionFromTableAttribute(node.getAttributes()) || hasThymeleafFragmentInsertionFromTableChildren(node.getChildren());
  }

  private static boolean hasThymeleafFragmentInsertionFromTableChildren(List<TagNode> nodes) {
    boolean hasInsertion = false;
    for (TagNode node : nodes) {
      if (node.getNodeName().startsWith("th:block")) {
        hasInsertion = hasInsertion || node.getAttributes().stream().map(Attribute::getName).anyMatch(THYMELEAF_FRAGMENT_INSERTION_KEYWORDS::contains);
      } else {
        hasInsertion = hasInsertion || node.getAttributes().stream().map(Attribute::getName).anyMatch(attributeName -> attributeName.equals("th:replace"));
      }
      hasInsertion = hasInsertion || hasThymeleafFragmentInsertionFromTableChildren(node.getChildren());
    }
    return hasInsertion;
  }

  private static boolean hasThymeleafFragmentInsertionFromTableAttribute(List<Attribute> tableAttributes) {
    return tableAttributes.stream().map(Attribute::getName).anyMatch(attributeName -> attributeName.equals("th:insert") || attributeName.equals("th:include"));
  }

  private static boolean hasHeader(TagNode node) {
    return node.getChildren().stream().anyMatch(TableWithoutHeaderCheck::isTableHeader) ||
      node.getChildren().stream().filter(child -> !isTable(child)).anyMatch(TableWithoutHeaderCheck::hasHeader);
  }

  private static boolean isTableHeader(TagNode node) {
    return "TH".equalsIgnoreCase(node.getNodeName());
  }
}
