/*
 * SonarHTML :: SonarQube Plugin
 * Copyright (c) 2010-2019 SonarSource SA and Matthijs Galesloot
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

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.CheckForNull;
import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "InputWithoutLabelCheck")
public class InputWithoutLabelCheck extends AbstractPageCheck {

  private static final Set<String> EXCLUDED_TYPES = new HashSet<>(Arrays.asList("SUBMIT", "BUTTON", "IMAGE", "HIDDEN"));

  private final Set<String> labelFor = new HashSet<>();
  private final Map<String, TagNode> inputIdToNode = new HashMap<>();
  private Deque<TagNode> elementStack;
  private Set<String> ids;
  private Map<TagNode, Set<String>> expectedIds;

  @Override
  public void startDocument(List<Node> nodes) {
    labelFor.clear();
    inputIdToNode.clear();
    elementStack = new ArrayDeque<>();
    ids = new HashSet<>();
    expectedIds = new HashMap<>();
  }

  @Override
  public void startElement(TagNode node) {
    if (isLabel(node) || insideLabelNode()) {
      elementStack.push(node);
    }
    if (getNodeId(node) != null) {
      ids.add(getNodeId(node));
    }
    if (isInputRequiredLabel(node) || isSelect(node) || isTextarea(node)) {
      if (node.getPropertyValue("aria-label") != null || insideLabelNode()) {
        return;
      }
      if (node.getPropertyValue("aria-labelledby") != null) {
        expectedIds.put(node, Arrays.stream(node.getPropertyValue("aria-labelledby").split(" ")).collect(Collectors.toSet()));
        return;
      }
      String id = getNodeId(node);

      if (id == null) {
        createViolation(node, "Add an \"id\" attribute to this input field and associate it with a label.");
      } else {
        inputIdToNode.put(id, node);
      }
    } else if (isLabel(node) && node.getAttribute("for") != null) {
      labelFor.add(node.getAttribute("for"));
    }
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
    String type = node.getAttribute("type");

    return type == null ||
      EXCLUDED_TYPES.contains(type.toUpperCase(Locale.ENGLISH));
  }

  private static boolean isLabel(TagNode node) {
    return "LABEL".equalsIgnoreCase(node.getNodeName());
  }

  @Override
  public void endDocument() {
    for (Map.Entry<String, TagNode> entry : inputIdToNode.entrySet()) {
      if (!labelFor.contains(entry.getKey())) {
        createViolation(entry.getValue(), "Associate a valid label to this input field.");
      }
    }
    expectedIds.forEach((node, expected) -> {
      if (!ids.containsAll(expected)) {
        String missingIds = expected.stream().filter(id -> !ids.contains(id)).map(s -> "\"" + s + "\"").collect(Collectors.joining(","));
        createViolation(node, "Use valid ids in \"aria-labelledby\" attribute. Following ids were not found: " + missingIds + ".");
      }
    });
  }

  @CheckForNull
  private static String getNodeId(TagNode node) {
    return node.getPropertyValue("ID");
  }

}
