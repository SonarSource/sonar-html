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
package org.sonar.plugins.html.checks.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;

public abstract class AbstractIllegalElementCheck extends AbstractPageCheck {

  private static final String DEFAULT_ELEMENTS = "";

  @RuleProperty(
    key = "elements",
    description = "Comma-separated list of names of forbidden elements",
    defaultValue = DEFAULT_ELEMENTS)
  public String elements = DEFAULT_ELEMENTS;

  private final Map<String, List<ConfiguredElement>> elementsByNormalizedName = new HashMap<>();

  private static final class ConfiguredElement {
    private final int order;
    private final String value;

    private ConfiguredElement(int order, String value) {
      this.order = order;
      this.value = value;
    }
  }

  @Override
  public void startDocument(List<Node> nodes) {
    elementsByNormalizedName.clear();

    int order = 0;
    for (String elementName : trimSplitCommaSeparatedList(elements)) {
      elementsByNormalizedName
        .computeIfAbsent(normalizeName(elementName), key -> new ArrayList<>())
        .add(new ConfiguredElement(order, elementName));
      order++;
    }
  }

  @Override
  public void startElement(TagNode element) {
    List<ConfiguredElement> localNameMatches = elementsByNormalizedName.get(normalizeName(element.getLocalName()));
    List<ConfiguredElement> nodeNameMatches = elementsByNormalizedName.get(normalizeName(element.getNodeName()));

    if (localNameMatches == null && nodeNameMatches == null) {
      return;
    }
    if (localNameMatches == nodeNameMatches) {
      createViolations(element, localNameMatches);
    } else if (localNameMatches == null) {
      createViolations(element, nodeNameMatches);
    } else if (nodeNameMatches == null) {
      createViolations(element, localNameMatches);
    } else {
      createViolations(element, localNameMatches, nodeNameMatches);
    }
  }

  private static String normalizeName(String elementName) {
    return elementName == null ? null : elementName.toLowerCase(Locale.ROOT);
  }

  private static String violationMessage(String elementName) {
    return "Remove this \"" + elementName + "\" element.";
  }

  private void createViolations(TagNode element, List<ConfiguredElement> matches) {
    for (ConfiguredElement configuredElement : matches) {
      createViolation(element, violationMessage(configuredElement.value));
    }
  }

  private void createViolations(TagNode element, List<ConfiguredElement> firstMatches, List<ConfiguredElement> secondMatches) {
    int firstIndex = 0;
    int secondIndex = 0;

    while (firstIndex < firstMatches.size() || secondIndex < secondMatches.size()) {
      if (secondIndex >= secondMatches.size() ||
        (firstIndex < firstMatches.size() && firstMatches.get(firstIndex).order < secondMatches.get(secondIndex).order)) {
        createViolation(element, violationMessage(firstMatches.get(firstIndex).value));
        firstIndex++;
      } else {
        createViolation(element, violationMessage(secondMatches.get(secondIndex).value));
        secondIndex++;
      }
    }
  }

}
