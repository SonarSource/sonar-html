/*
 * SonarQube HTML
 * Copyright (C) 2010-2025 SonarSource SA
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
package org.sonar.plugins.html.checks.dependencies;

import java.util.List;
import java.util.regex.Pattern;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Attribute;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "IllegalNamespaceCheck")
public class IllegalNamespaceCheck extends AbstractPageCheck {

  private static final Pattern XMLNS_PREFIX = Pattern.compile("^xmlns", Pattern.CASE_INSENSITIVE);
  private static final String DEFAULT_NAMESPACES = "";

  @RuleProperty(
    key = "namespaces",
    description = "Comma separated list of namespaces",
    defaultValue = DEFAULT_NAMESPACES)
  public String namespaces = DEFAULT_NAMESPACES;

  private String[] namespacesArray;

  @Override
  public void startDocument(List<Node> nodes) {
    namespacesArray = namespaces.isEmpty() ? new String[0] : namespaces.split(",");
  }

  @Override
  public void startElement(TagNode element) {
    for (Attribute a : element.getAttributes()) {
      if (XMLNS_PREFIX.matcher(a.getName()).find()) {
        for (String namespace : namespacesArray) {
          if (a.getValue().equalsIgnoreCase(namespace)) {
            createViolation(element.getStartLinePosition(), "Using \"" + a.getValue() + "\" namespace is not allowed.");
          }
        }
      }
    }
  }

}
