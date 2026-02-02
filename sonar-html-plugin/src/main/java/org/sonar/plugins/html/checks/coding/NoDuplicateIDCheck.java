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
package org.sonar.plugins.html.checks.coding;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.sonar.check.Rule;
import org.sonar.plugins.html.api.Helpers;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;
import org.sonar.plugins.html.node.TextNode;

/**
 * Rule to detect duplicate HTML id attributes.
 *
 * To reduce false positives, this rule:
 * 1. Is lenient with IDs inside conditional blocks (e.g., @if/@else, v-if/v-else, c:if, {% if %}).
 *    IDs inside conditionals are only checked against IDs found outside any conditional block.
 * 2. Ignores IDs that contain dynamic/template expressions (e.g., @variable, {{expression}}, ${var})
 *    since these will be unique at runtime.
 */
@Rule(key = "S7930")
public class NoDuplicateIDCheck extends AbstractPageCheck {

  /**
   * JSP JSTL conditional tags.
   */
  private static final Set<String> JSTL_CONDITIONAL_TAGS = Set.of(
    "c:if", "c:when", "c:otherwise", "c:choose"
  );

  /**
   * Pattern to detect start of conditional blocks in text (Angular, Razor, Twig/Jinja, PHP).
   * Matches: @if(), @switch(), @case, @default, {% if %}, {% for %}, <?php if/foreach/for
   */
  private static final Pattern CONDITIONAL_START_PATTERN = Pattern.compile(
    "@(if|switch)\\s*\\(|" +
    "@(case|default)\\s*[({]|" +
    "\\{%[-\\s]*(if|for)\\b|" +
    "<\\?(?:php)?\\s*(if|foreach|for)\\b",
    Pattern.CASE_INSENSITIVE
  );

  /**
   * Pattern to detect end of conditional blocks in text.
   * Matches: {% endif %}, {% endfor %}, <?php endif/endforeach/endfor
   */
  private static final Pattern CONDITIONAL_END_PATTERN = Pattern.compile(
    "\\{%[-\\s]*(endif|endfor)\\b|" +
    "<\\?(?:php)?\\s*(endif|endforeach|endfor)\\b",
    Pattern.CASE_INSENSITIVE
  );

  /**
   * Vue.js conditional directives.
   */
  private static final Set<String> VUE_CONDITIONAL_ATTRS = Set.of(
    "v-if", "v-else-if", "v-else", "v-for"
  );

  /**
   * Angular structural directives for conditionals.
   */
  private static final Set<String> ANGULAR_CONDITIONAL_ATTRS = Set.of(
    "*ngIf", "*ngFor", "*ngSwitchCase", "*ngSwitchDefault"
  );

  // IDs seen outside any conditional - these are the "authoritative" IDs
  private final Map<String, Integer> unconditionalIds = new HashMap<>();

  // Depth counter for nested conditionals (text-based: Razor, Twig, PHP, Angular new syntax)
  private int textConditionalDepth = 0;

  // Depth counter for tag-based conditionals (JSP JSTL)
  private int tagConditionalDepth = 0;

  @Override
  public void startDocument(List<Node> nodes) {
    unconditionalIds.clear();
    textConditionalDepth = 0;
    tagConditionalDepth = 0;
  }

  @Override
  public void characters(TextNode textNode) {
    String text = textNode.getCode();

    // Count conditional starts
    var startMatcher = CONDITIONAL_START_PATTERN.matcher(text);
    while (startMatcher.find()) {
      textConditionalDepth++;
    }

    // Count conditional ends
    var endMatcher = CONDITIONAL_END_PATTERN.matcher(text);
    while (endMatcher.find()) {
      if (textConditionalDepth > 0) {
        textConditionalDepth--;
      }
    }

    // Handle closing braces for Angular/Razor blocks
    // Count standalone closing braces that likely end conditional blocks
    if (textConditionalDepth > 0 && text.contains("}")) {
      // Simple heuristic: closing brace at start of text node often ends a block
      String trimmed = text.trim();
      if (trimmed.startsWith("}") && !trimmed.startsWith("}}")) {
        textConditionalDepth--;
      }
    }
  }

  @Override
  public void startElement(TagNode node) {
    String nodeName = node.getNodeName().toLowerCase(Locale.ROOT);

    // Track JSP JSTL conditional tag depth
    if (JSTL_CONDITIONAL_TAGS.contains(nodeName)) {
      tagConditionalDepth++;
    }

    // Check for ID attribute
    var idValue = node.getAttribute("id");
    if (idValue != null && !idValue.isEmpty()) {
      // Skip dynamic IDs - they will be unique at runtime
      if (Helpers.isDynamicValue(idValue, getHtmlSourceCode())) {
        return;
      }

      boolean inConditional = isInConditional(node);

      if (inConditional) {
        // Inside a conditional: only check against unconditional IDs
        if (unconditionalIds.containsKey(idValue)) {
          createViolation(node,
            String.format("Duplicate id \"%s\" found. First occurrence was on line %d.",
              idValue, unconditionalIds.get(idValue)));
        }
        // Don't store - IDs in conditionals don't create new "authoritative" entries
      } else {
        // Outside conditionals: normal duplicate check and storage
        if (unconditionalIds.containsKey(idValue)) {
          createViolation(node,
            String.format("Duplicate id \"%s\" found. First occurrence was on line %d.",
              idValue, unconditionalIds.get(idValue)));
        } else {
          unconditionalIds.put(idValue, node.getStartLinePosition());
        }
      }
    }
  }

  @Override
  public void endElement(TagNode node) {
    String nodeName = node.getNodeName().toLowerCase(Locale.ROOT);

    // Track JSP JSTL conditional tag depth
    if (JSTL_CONDITIONAL_TAGS.contains(nodeName) && tagConditionalDepth > 0) {
      tagConditionalDepth--;
    }
  }

  /**
   * Determines if an element is inside a conditional block.
   */
  private boolean isInConditional(TagNode node) {
    // Check text-based conditional depth (Angular, Razor, Twig, PHP)
    if (textConditionalDepth > 0) {
      return true;
    }

    // Check tag-based conditional depth (JSP JSTL)
    if (tagConditionalDepth > 0) {
      return true;
    }

    // Check for Vue conditional attributes on the element itself
    for (String attr : VUE_CONDITIONAL_ATTRS) {
      if (node.hasAttribute(attr)) {
        return true;
      }
    }

    // Check for Angular structural directives on the element itself
    for (String attr : ANGULAR_CONDITIONAL_ATTRS) {
      if (node.hasAttribute(attr)) {
        return true;
      }
    }

    return false;
  }
}
