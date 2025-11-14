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
package org.sonar.plugins.html.checks.coding;

import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Rule(key="S7930")
public class NoDuplicateIDCheck extends AbstractPageCheck {

  private final Map<String, Integer> seenIds = new HashMap<>();

  @Override
  public void startDocument(List<Node> nodes) {
    seenIds.clear();
  }

  @Override
  public void startElement(TagNode node) {
    var idValue = node.getAttribute("id");
    if (idValue != null && !idValue.isEmpty()) {
      // Case-sensitive comparison
      if (seenIds.containsKey(idValue)) {
        createViolation(node,
                String.format("Duplicate id \"%s\" found. First occurrence was on line %d.",
                        idValue, seenIds.get(idValue)));
      } else {
        seenIds.put(idValue, node.getStartLinePosition());
      }
    }
  }
}
