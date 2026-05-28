/*
 * SonarQube HTML
 * Copyright (C) SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * You can redistribute and/or modify this program under the terms of
 * the Sonar Source-Available License Version 1, as published by SonarSource Sàrl.
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

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.CheckForNull;
import org.sonar.check.Rule;
import org.sonar.plugins.html.api.Helpers;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "InputWithoutLabelCheck")
public class InputWithoutLabelCheck extends AbstractPageCheck {

  private static final Set<String> EXCLUDED_TYPES = Set.of("SUBMIT", "BUTTON", "IMAGE", "HIDDEN");
  private static final String ADD_ID_MESSAGE = "Add an \"id\" attribute to this input field and associate it with a label.";
  private static final String ASSOCIATE_LABEL_MESSAGE = "Associate a valid label to this input field.";

  private final Set<String> labelTargets = new LinkedHashSet<>();
  private final Map<TagNode, Set<String>> controlsWithTargets = new LinkedHashMap<>();
  private Deque<TagNode> elementStack;
  private Set<String> ids;
  private Map<TagNode, Set<String>> expectedIds;

  @Override
  public void startDocument(List<Node> nodes) {
    labelTargets.clear();
    controlsWithTargets.clear();
    elementStack = new ArrayDeque<>();
    ids = new LinkedHashSet<>();
    expectedIds = new LinkedHashMap<>();
  }

  @Override
  public void startElement(TagNode node) {
    if (isLabel(node) || insideLabelNode()) {
      elementStack.push(node);
    }

    String nodeId = getNodeId(node);
    if (nodeId != null) {
      ids.add(nodeId);
    }

    if (isInputRequiredLabel(node) || isSelect(node) || isTextarea(node)) {
      registerControl(node);
    } else if (isLabel(node)) {
      labelTargets.addAll(getLabelTargets(node));
    }
  }

  private void registerControl(TagNode node) {
    if (node.hasProperty("aria-label") || insideLabelNode()) {
      return;
    }

    String ariaLabelledBy = getTrimmedPropertyValue(node, "aria-labelledby");
    if (ariaLabelledBy != null) {
      if (hasDynamicAriaLabelledBy(node, ariaLabelledBy)) {
        return;
      }

      Set<String> referencedIds = Arrays.stream(ariaLabelledBy.split("\\s+"))
        .filter(id -> !id.isEmpty())
        .collect(Collectors.toCollection(LinkedHashSet::new));
      if (!referencedIds.isEmpty()) {
        expectedIds.put(node, referencedIds);
        return;
      }
    }

    Set<String> controlTargets = getControlTargets(node);
    if (controlTargets.isEmpty()) {
      createViolation(node, ADD_ID_MESSAGE);
      return;
    }
    controlsWithTargets.put(node, controlTargets);
  }

  @Override
  public void endElement(TagNode node) {
    if (insideLabelNode()) {
      // close all elements until we find matching element or stack is empty
      TagNode pop = elementStack.pop();
      while (!pop.equalsElementName(node.getNodeName()) && !elementStack.isEmpty()) {
        pop = elementStack.pop();
      }
    }
  }

  private boolean insideLabelNode() {
    return !elementStack.isEmpty();
  }

  private static boolean isSelect(TagNode node) {
    return isType(node, "SELECT");
  }

  private static boolean isTextarea(TagNode node) {
    return isType(node, "TEXTAREA");
  }

  private static boolean isInputRequiredLabel(TagNode node) {
    return isType(node, "INPUT") &&
      !hasExcludedType(node);
  }

  private static boolean isType(TagNode node, String type) {
    return type.equalsIgnoreCase(node.getNodeName());
  }

  private static boolean hasExcludedType(TagNode node) {
    String type = getTrimmedPropertyValue(node, "type");

    return type != null &&
      EXCLUDED_TYPES.contains(type.toUpperCase(Locale.ENGLISH));
  }

  private static boolean isLabel(TagNode node) {
    return "LABEL".equalsIgnoreCase(node.getNodeName());
  }

  @Override
  public void endDocument() {
    controlsWithTargets.forEach((node, targets) -> {
      if (targets.stream().noneMatch(labelTargets::contains)) {
        createViolation(node, ASSOCIATE_LABEL_MESSAGE);
      }
    });
    expectedIds.forEach((node, expected) -> {
      if (!ids.containsAll(expected)) {
        String missingIds = expected.stream()
          .filter(id -> !ids.contains(id))
          .map(id -> "\"" + id + "\"")
          .collect(Collectors.joining(","));
        createViolation(node, "Use valid ids in \"aria-labelledby\" attribute. Following ids were not found: " + missingIds + ".");
      }
    });
  }

  private boolean hasDynamicAriaLabelledBy(TagNode node, String ariaLabelledBy) {
    var property = node.getProperty("aria-labelledby");
    return property != null &&
      (!"aria-labelledby".equalsIgnoreCase(property.getName()) || Helpers.containsDynamicValue(ariaLabelledBy, getHtmlSourceCode()));
  }

  private static Set<String> getControlTargets(TagNode node) {
    Set<String> targets = new LinkedHashSet<>();
    addTarget(targets, getNodeId(node));
    return targets;
  }

  private static Set<String> getLabelTargets(TagNode node) {
    Set<String> targets = new LinkedHashSet<>();
    addTarget(targets, getTrimmedPropertyValue(node, "for"));
    return targets;
  }

  private static void addTarget(Set<String> targets, @CheckForNull String target) {
    if (target != null) {
      targets.add(target);
    }
  }

  @CheckForNull
  private static String getNodeId(TagNode node) {
    return getTrimmedPropertyValue(node, "id");
  }

  @CheckForNull
  private static String getTrimmedPropertyValue(TagNode node, String propertyName) {
    String value = node.getPropertyValue(propertyName);
    if (value == null) {
      return null;
    }

    String trimmedValue = value.trim();
    if (trimmedValue.isEmpty()) {
      return null;
    }

    if (trimmedValue.length() >= 2 &&
      ((trimmedValue.startsWith("'") && trimmedValue.endsWith("'")) || (trimmedValue.startsWith("\"") && trimmedValue.endsWith("\"")))) {
      trimmedValue = trimmedValue.substring(1, trimmedValue.length() - 1).trim();
    }

    return trimmedValue.isEmpty() ? null : trimmedValue;
  }

}
