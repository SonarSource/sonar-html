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

  private static boolean hasHeader(TagNode node) {
    return node.getChildren().stream().anyMatch(TableWithoutHeaderCheck::isTableHeader) ||
      node.getChildren().stream().filter(child -> !isTable(child)).anyMatch(TableWithoutHeaderCheck::hasHeader);
  }

  private static boolean isTableHeader(TagNode node) {
    return "TH".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean hasThymeleafFragmentInsertion(TagNode node) {
    return hasThymeleafFragmentInsertionFromTableAttribute(node.getAttributes()) || hasThymeleafFragmentInsertionFromTableChildren(node.getChildren());
  }

  private static boolean hasThymeleafFragmentInsertionFromTableAttribute(List<Attribute> tableAttributes) {
    return tableAttributes.stream().map(Attribute::getName).anyMatch(attributeName -> attributeName.equals("th:insert") || attributeName.equals("th:include"));
  }

  private static boolean hasThymeleafFragmentInsertionFromTableChildren(List<TagNode> nodes) {
    for (TagNode node : nodes) {
      if (node.getAttributes().stream().map(Attribute::getName).anyMatch(THYMELEAF_FRAGMENT_INSERTION_KEYWORDS::contains)
        || hasThymeleafFragmentInsertionFromTableChildren(node.getChildren())) {
        return true;
      }
    }
    return false;
  }
}
