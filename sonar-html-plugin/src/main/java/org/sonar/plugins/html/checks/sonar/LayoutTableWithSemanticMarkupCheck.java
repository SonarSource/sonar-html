/*
 * SonarQube HTML
 * Copyright (C) 2010-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource Sàrl.
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

import static java.lang.String.format;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Attribute;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "S5258")
public class LayoutTableWithSemanticMarkupCheck extends AbstractPageCheck {

  private Deque<Boolean> isWithinLayoutTable = new LinkedList<>();

  @Override
  public void startDocument(List<Node> nodes) {
    isWithinLayoutTable.clear();
  }

  @Override
  public void endDocument() {
    isWithinLayoutTable.clear();
  }

  @Override
  public void startElement(TagNode node) {
    if (isTable(node)) {
      isWithinLayoutTable.addFirst(isLayout(node));
      if (Boolean.TRUE.equals(isWithinLayoutTable.peekFirst())) {
        raiseViolationOnAttribute(node, "SUMMARY");
      }
    }
    if (Boolean.TRUE.equals(isWithinLayoutTable.peekFirst())) {
      if (isCaption(node) || isTableHeader(node)) {
        createViolation(node, format("Remove this \"%s\" element", node.getNodeName()));
      } else if (isTableColumn(node)) {
        raiseViolationOnAttribute(node, "HEADERS");
        raiseViolationOnAttribute(node, "SCOPE");
      }
    }
  }

  @Override
  public void endElement(TagNode node) {
    if (isTable(node) && !isWithinLayoutTable.isEmpty()) {
      isWithinLayoutTable.removeFirst();
    }
  }

  private void raiseViolationOnAttribute(TagNode node, String attributeName) {
    findAttribute(node, attributeName).ifPresent(attribute ->
        createViolation(attribute.getLine(), format("Remove this \"%s\" attribute", attribute.getName())));
  }


  private static boolean isTable(TagNode node) {
    return "TABLE".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isCaption(TagNode node) {
    return "CAPTION".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isTableHeader(TagNode node) {
    return "TH".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isTableColumn(TagNode node) {
    return "TD".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isLayout(TagNode node) {
    String role = node.getAttribute("role");
    return role != null && ("PRESENTATION".equalsIgnoreCase(role) || "NONE".equalsIgnoreCase(role));
  }

  private static Optional<Attribute> findAttribute(TagNode node, String attributeName) {
    return node.getAttributes().stream().filter(a -> attributeName.equalsIgnoreCase(a.getName())).findAny();
  }
}
