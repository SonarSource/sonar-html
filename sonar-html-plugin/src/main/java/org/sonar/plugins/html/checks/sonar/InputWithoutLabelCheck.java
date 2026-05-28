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
  private static final String ID = "id";
  private static final String ASP_FOR = "asp-for";

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
      String target = resolveStaticLabelTarget(node);
      if (target != null) {
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
      // aria-labelledby value cannot be statically resolved (binding form, JS expression, server-side marker) —
      // trust the author rather than guess.
      return;
    }
    if (!ariaReferences.isEmpty()) {
      expectedIds.put(node, ariaReferences);
      return;
    }
    // aria-labelledby absent or empty — fall back to the id/label-for association check.

    String controlTarget = resolveStaticControlId(node);
    if (controlTarget != null) {
      controlsWithTargets.put(node, controlTarget);
      return;
    }
    if (!hasAnyControlIdHint(node)) {
      createViolation(node, ADD_ID_MESSAGE);
    }
    // else: the control has an id-bearing attribute we cannot statically resolve (e.g. [id]="x", id="${userId}") —
    // its runtime value is opaque to us, so we trust it.
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
  // Unlike id/for, `type` values are essentially an enum (text/hidden/submit/...), so we read binding-form values
  // (e.g. [type]="hidden") at face value and strip any inner JS quotes ([type]="'hidden'") to a literal.
  private static boolean hasExcludedType(TagNode node) {
    String type = looseTypeValue(node);

    return type != null &&
      EXCLUDED_TYPES.contains(type.toUpperCase(Locale.ENGLISH));
  }

  @CheckForNull
  private static String looseTypeValue(TagNode node) {
    String value = node.getPropertyValue("type");
    if (value == null) {
      return null;
    }
    String trimmed = value.trim();
    if (trimmed.length() >= 2
      && ((trimmed.startsWith("'") && trimmed.endsWith("'")) || (trimmed.startsWith("\"") && trimmed.endsWith("\"")))) {
      trimmed = trimmed.substring(1, trimmed.length() - 1).trim();
    }
    return trimmed.isEmpty() ? null : trimmed;
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
   *   <li>{@code null} — the value cannot be resolved statically (Angular/Vue binding form, or a server-side
   *       template marker in the plain attribute); the caller should trust the author.</li>
   *   <li>empty set — the attribute is absent or empty; the caller should fall back to the {@code id}/{@code for}
   *       association check.</li>
   *   <li>non-empty set — statically resolved ids to validate against the document's known ids.</li>
   * </ul>
   */
  @CheckForNull
  private Set<String> resolveAriaLabelledByReferences(TagNode node) {
    Attribute property = node.getProperty("aria-labelledby");
    if (property == null) {
      return Set.of();
    }
    if (isBindingForm(property, "aria-labelledby")) {
      return null;
    }
    String trimmed = trimmedOrNull(property.getValue());
    if (trimmed == null) {
      return Set.of();
    }
    if (isDynamic(trimmed)) {
      return null;
    }
    return Arrays.stream(trimmed.split("\\s+"))
      .filter(id -> !id.isEmpty())
      .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  /**
   * Returns the statically-known id this control announces (via plain {@code id} or Razor {@code asp-for}),
   * or {@code null} when the value is opaque to static analysis — Angular/Vue binding form, a server-side
   * template expression, or simply absent. Callers should check {@link #hasAnyControlIdHint(TagNode)} to
   * distinguish "opaque value present" from "no id-bearing attribute at all".
   */
  @CheckForNull
  private String resolveStaticControlId(TagNode node) {
    Attribute idProperty = node.getProperty(ID);
    if (idProperty != null) {
      return resolveStaticAttribute(idProperty, ID);
    }
    return staticAttributeValue(node, ASP_FOR);
  }

  /**
   * Returns the statically-known id this label points at (via plain {@code for}, JSX {@code htmlFor}, or
   * Razor {@code asp-for}), or {@code null} when the value is opaque (binding form, server-side expression,
   * or absent). Labels with opaque {@code for} values do not contribute to the set of known label targets.
   */
  @CheckForNull
  private String resolveStaticLabelTarget(TagNode node) {
    Attribute forProperty = node.getProperty("for");
    if (forProperty != null) {
      return resolveStaticAttribute(forProperty, "for");
    }
    String htmlFor = staticAttributeValue(node, "htmlFor");
    if (htmlFor != null) {
      return htmlFor;
    }
    return staticAttributeValue(node, ASP_FOR);
  }

  @CheckForNull
  private String resolveStaticAttribute(Attribute attribute, String canonicalName) {
    if (isBindingForm(attribute, canonicalName)) {
      return null;
    }
    return staticOrNull(attribute.getValue());
  }

  @CheckForNull
  private String staticAttributeValue(TagNode node, String attributeName) {
    return staticOrNull(node.getAttribute(attributeName));
  }

  @CheckForNull
  private String staticOrNull(@CheckForNull String rawValue) {
    String trimmed = trimmedOrNull(rawValue);
    if (trimmed == null || isDynamic(trimmed)) {
      return null;
    }
    return trimmed;
  }

  private boolean isDynamic(String value) {
    return Helpers.containsDynamicValue(value, getHtmlSourceCode());
  }

  private static boolean isBindingForm(Attribute attribute, String canonicalName) {
    return !canonicalName.equalsIgnoreCase(attribute.getName());
  }

  private static boolean hasAnyControlIdHint(TagNode node) {
    return node.hasProperty(ID) || node.hasAttribute(ASP_FOR);
  }

  @CheckForNull
  private static String getNodeId(TagNode node) {
    return staticPropertyValue(node, ID);
  }

  @CheckForNull
  private static String staticPropertyValue(TagNode node, String propertyName) {
    Attribute property = node.getProperty(propertyName);
    if (property == null || isBindingForm(property, propertyName)) {
      return null;
    }
    return trimmedOrNull(property.getValue());
  }

  @CheckForNull
  private static String trimmedOrNull(@CheckForNull String value) {
    if (value == null) {
      return null;
    }

    String trimmedValue = value.trim();
    return trimmedValue.isEmpty() ? null : trimmedValue;
  }

}
