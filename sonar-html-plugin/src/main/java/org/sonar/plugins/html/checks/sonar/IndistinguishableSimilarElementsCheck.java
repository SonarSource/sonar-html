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
package org.sonar.plugins.html.checks.sonar;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "S5255")
public class IndistinguishableSimilarElementsCheck extends AbstractPageCheck {

  private static final List<String> LANDMARK_ROLES = List.of(
    "BANNER", "COMPLEMENTARY", "CONTENTINFO", "FORM", "MAIN", "NAVIGATION", "SEARCH", "APPLICATION"
  );

  private List<TagNode> navs = new LinkedList<>();
  private List<TagNode> asides = new LinkedList<>();
  private List<TagNode> elements = new LinkedList<>();

  @Override
  public void startDocument(List<Node> nodes) {
    navs.clear();
    asides.clear();
    elements.clear();
  }

  @Override
  public void endDocument() {
    raiseViolationOnMissingAriaLabel(navs);
    navs.clear();

    raiseViolationOnMissingAriaLabel(asides);
    asides.clear();

    raiseViolationOnDuplicateLandmarkRole(elements);
    elements.clear();
  }

  @Override
  public void startElement(TagNode node) {
    if (isNav(node)) {
      navs.add(node);
    } else if (isAside(node)) {
      asides.add(node);
    } else if (hasLandmarkRole(node)) {
      elements.add(node);
    }
  }

  private void raiseViolationOnMissingAriaLabel(List<TagNode> nodes) {
    if (nodes.size() > 1) {
      nodes.stream().filter(node -> !hasAriaLabel(node)).forEach(node ->
        createViolation(node, "Add an \"aria-label\" or \"aria-labbelledby\" attribute to this element."));
    }
  }

  private void raiseViolationOnDuplicateLandmarkRole(List<TagNode> nodes) {
    Map<String, List<TagNode>> matched = nodes.stream().collect(Collectors.groupingBy(node -> node.getAttribute("ROLE").toUpperCase(Locale.ROOT)));
    for (List<TagNode> matches : matched.values()) {
      List<TagNode> labeless = matches.stream().filter(match -> !hasAriaLabel(match)).toList();
      if (labeless.size() > 1 || matches.size() > 1) {
        for (TagNode node : labeless) {
          createViolation(node, "Add an \"aria-label\" or \"aria-labbelledby\" attribute to this element.");
        }
      }
    }
  }

  private static boolean isNav(TagNode node) {
    return "NAV".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isAside(TagNode node) {
    return "ASIDE".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean hasLandmarkRole(TagNode node) {
    return LANDMARK_ROLES.stream().anyMatch(role -> role.equalsIgnoreCase(node.getPropertyValue("ROLE")));
  }

  private static boolean hasAriaLabel(TagNode node) {
    return node.hasProperty("ARIA-LABEL") || node.hasProperty("ARIA-LABELLEDBY");
  }
}
