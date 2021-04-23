/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (c) 2010-2021 SonarSource SA and Matthijs Galesloot
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

import java.util.Arrays;
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

  private static final List<String> LANDMARK_ROLES = Arrays.asList(
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
      List<TagNode> labeless = matches.stream().filter(match -> !hasAriaLabel(match)).collect(Collectors.toList());
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
