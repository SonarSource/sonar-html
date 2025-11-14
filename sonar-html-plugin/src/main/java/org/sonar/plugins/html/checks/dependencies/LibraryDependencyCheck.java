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
package org.sonar.plugins.html.checks.dependencies;

import java.util.Arrays;
import java.util.List;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Attribute;
import org.sonar.plugins.html.node.DirectiveNode;
import org.sonar.plugins.html.node.ExpressionNode;
import org.sonar.plugins.html.node.Node;

@Rule(key = "LibraryDependencyCheck")
public class LibraryDependencyCheck extends AbstractPageCheck {

  private static final String DEFAULT_LIBRARIES = "";
  private static final String DEFAULT_MESSAGE = "Remove the usage of this library which is not allowed.";

  @RuleProperty(
    key = "libraries",
    description = "Comma-separated list of Java packages or classes, such as java.sql or java.util.ArrayList",
    defaultValue = DEFAULT_LIBRARIES)
  public String libraries = DEFAULT_LIBRARIES;

  @RuleProperty(
    key = "message",
    description = "Issue message which is displayed in case of violation",
    defaultValue = "" + DEFAULT_MESSAGE)
  public String message = DEFAULT_MESSAGE;

  private List<String> librariesList;

  @Override
  public void startDocument(List<Node> nodes) {
    librariesList = Arrays.stream(libraries.split(","))
      .map(String::trim)
      .filter(s -> !s.isEmpty())
      .toList();
  }

  @Override
  public void directive(DirectiveNode node) {
    if (node.isJsp() && "page".equalsIgnoreCase(node.getNodeName())) {
      for (Attribute attribute : node.getAttributes()) {
        if (isIllegalImport(attribute)) {
          createViolation(node.getStartLinePosition(), message);
        }
      }
    }
  }

  private boolean isIllegalImport(Attribute a) {
    if ("import".equals(a.getName())) {
      for (String library : librariesList) {
        if (a.getValue().contains(library)) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public void expression(ExpressionNode node) {
    for (String library : librariesList) {
      if (node.getCode().contains(library)) {
        createViolation(node.getStartLinePosition(), message);
      }
    }
  }

}
