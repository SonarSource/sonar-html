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
import org.sonar.plugins.html.node.Attribute;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "InputWithoutLabelCheck")
public class InputWithoutLabelCheck extends AbstractPageCheck {

  private static final Set<String> EXCLUDED_TYPES = Set.of("SUBMIT", "BUTTON", "IMAGE", "HIDDEN");
  private static final String ADD_ID_MESSAGE = "Add an \"id\" attribute to this input field and associate it with a label.";
  private static final String ASSOCIATE_LABEL_MESSAGE = "Associate a valid label to this input field.";

  // LinkedHashMap/LinkedHashSet so endDocument emits issues in document order — matches tests and ruling reports.
  private final Set<String> labelTargets = new LinkedHashSet<>();
  private final Map<TagNode, String> controlsWithTargets = new LinkedHashMap<>();
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
      String target = getRawLabelTarget(node);
      if (target != null && !isDynamic(target)) {
        labelTargets.add(target);
      }
    }
  }

  private void registerControl(TagNode node) {
    if (node.hasProperty("aria-label") || insideLabelNode()) {
      return;
    }

    Set<String> ariaReferences = resolveAriaLabelledByReferences(node);
    if (ariaReferences == null) {
      // aria-labelledby is present but its value is a dynamic expression we cannot statically resolve — trust it.
      return;
    }
    if (!ariaReferences.isEmpty()) {
      expectedIds.put(node, ariaReferences);
      return;
    }
    // aria-labelledby absent or empty — fall back to the id/label-for association check.

    String rawId = getRawControlId(node);
    if (rawId == null) {
      createViolation(node, ADD_ID_MESSAGE);
      return;
    }
    if (isDynamic(rawId)) {
      // Dynamic id (server-side template expression) — trust the author rather than emit a misleading mismatch.
      return;
    }
    controlsWithTargets.put(node, rawId);
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

  // Per HTML5, an <input> without an explicit "type" defaults to "text" and therefore requires a label.
  // Missing or non-string "type" is treated as labelable — only inputs with an explicit excluded type are skipped.
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
    controlsWithTargets.forEach((node, target) -> {
      if (!labelTargets.contains(target)) {
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

  /**
   * Returns the ids referenced by {@code aria-labelledby}, distinguishing three outcomes:
   * <ul>
   *   <li>{@code null} — value is a dynamic expression we cannot resolve statically (binding-form identifier,
   *       JS expression, server-side template marker); the caller should trust the author and skip validation.</li>
   *   <li>empty set — the attribute is absent or empty; the caller should fall back to the {@code id}/{@code for}
   *       association check.</li>
   *   <li>non-empty set — statically resolved ids to validate against the document's known ids.</li>
   * </ul>
   * For binding-form names ({@code [aria-labelledby]}, {@code :aria-labelledby}, {@code v-bind:aria-labelledby}) the
   * value is a JS expression — we only treat it as static when it is a quoted string literal (e.g. {@code "'foo'"}).
   */
  @CheckForNull
  private Set<String> resolveAriaLabelledByReferences(TagNode node) {
    Attribute property = node.getProperty("aria-labelledby");
    if (property == null) {
      return Set.of();
    }
    String rawValue = property.getValue();
    String trimmed = rawValue == null ? "" : rawValue.trim();
    if (trimmed.isEmpty()) {
      return Set.of();
    }
    boolean bindingForm = !"aria-labelledby".equalsIgnoreCase(property.getName());
    if (bindingForm) {
      if (!isQuotedStringLiteral(trimmed)) {
        return null;
      }
      trimmed = trimmed.substring(1, trimmed.length() - 1).trim();
      if (trimmed.isEmpty()) {
        return Set.of();
      }
    }
    if (Helpers.containsDynamicValue(trimmed, getHtmlSourceCode())) {
      return null;
    }
    return Arrays.stream(trimmed.split("\\s+"))
      .filter(id -> !id.isEmpty())
      .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  private static boolean isQuotedStringLiteral(String value) {
    return value.length() >= 2
      && ((value.startsWith("'") && value.endsWith("'")) || (value.startsWith("\"") && value.endsWith("\"")));
  }

  /**
   * Returns the raw id value this control announces — via {@code id} (incl. Angular/Vue binding forms, handled by
   * {@link TagNode#getProperty(String)}) or via the Razor {@code asp-for} tag-helper, which generates an
   * {@code id} matching the bound model property. Returns {@code null} when no id-bearing attribute is set.
   * Callers must apply {@link #isDynamic(String)} to decide whether to trust the value.
   */
  @CheckForNull
  private static String getRawControlId(TagNode node) {
    String value = getTrimmedPropertyValue(node, "id");
    if (value == null) {
      value = getTrimmedAttributeValue(node, "asp-for");
    }
    return value;
  }

  /**
   * Returns the raw id this label points at — via {@code for} (incl. Angular/Vue binding forms), the JSX
   * {@code htmlFor} alias, or the Razor {@code asp-for} tag-helper (which generates a {@code for} matching the
   * bound model property). Returns {@code null} when no for-bearing attribute is set. Callers must apply
   * {@link #isDynamic(String)} to decide whether to trust the value.
   */
  @CheckForNull
  private static String getRawLabelTarget(TagNode node) {
    String value = getTrimmedPropertyValue(node, "for");
    if (value == null) {
      value = getTrimmedAttributeValue(node, "htmlFor");
    }
    if (value == null) {
      value = getTrimmedAttributeValue(node, "asp-for");
    }
    return value;
  }

  private boolean isDynamic(String value) {
    return Helpers.containsDynamicValue(value, getHtmlSourceCode());
  }

  @CheckForNull
  private static String getNodeId(TagNode node) {
    return getTrimmedPropertyValue(node, "id");
  }

  /**
   * Reads a property via {@link TagNode#getProperty(String)} (so Angular/Vue binding forms are matched) and
   * normalizes the value: trims whitespace and, when the value is wrapped in matching single or double quotes,
   * strips them. The quote-stripping handles binding-form JS string literals like {@code [type]="'text'"} —
   * outer HTML quotes are removed by the tokenizer, but inner JS quotes survive in the stored value.
   */
  @CheckForNull
  private static String getTrimmedPropertyValue(TagNode node, String propertyName) {
    return trimmedOrNull(node.getPropertyValue(propertyName));
  }

  @CheckForNull
  private static String getTrimmedAttributeValue(TagNode node, String attributeName) {
    return trimmedOrNull(node.getAttribute(attributeName));
  }

  @CheckForNull
  private static String trimmedOrNull(@CheckForNull String value) {
    if (value == null) {
      return null;
    }

    String trimmedValue = value.trim();
    if (trimmedValue.isEmpty()) {
      return null;
    }

    if (isQuotedStringLiteral(trimmedValue)) {
      trimmedValue = trimmedValue.substring(1, trimmedValue.length() - 1).trim();
    }

    return trimmedValue.isEmpty() ? null : trimmedValue;
  }

}
