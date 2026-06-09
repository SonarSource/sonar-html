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
package org.sonar.plugins.html.checks.coding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.sonar.check.Rule;
import org.sonar.plugins.html.api.Helpers;
import org.sonar.plugins.html.api.TemplateConditionalScopeTracker;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.DirectiveNode;
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

  // IDs seen outside any conditional - these are the "authoritative" IDs
  private final Map<String, Integer> unconditionalIds = new HashMap<>();
  private final TemplateConditionalScopeTracker conditionalScope = new TemplateConditionalScopeTracker();

  @Override
  public void startDocument(List<Node> nodes) {
    unconditionalIds.clear();
    conditionalScope.reset();
  }

  @Override
  public void characters(TextNode textNode) {
    conditionalScope.visitText(textNode);
  }

  @Override
  public void directive(DirectiveNode directiveNode) {
    conditionalScope.visitDirective(directiveNode);
  }

  @Override
  public void startElement(TagNode node) {
    conditionalScope.startElement(node);
    handleIdAttribute(node);
  }

  @Override
  public void endElement(TagNode node) {
    conditionalScope.endElement(node);
  }

  private void handleIdAttribute(TagNode node) {
    String idValue = node.getAttribute("id");
    if (shouldIgnoreId(idValue)) {
      return;
    }
    if (conditionalScope.isInConditional(node)) {
      reportDuplicateAgainstUnconditionalId(node, idValue);
    } else {
      registerUnconditionalId(node, idValue);
    }
  }

  private boolean shouldIgnoreId(String idValue) {
    return idValue == null
      || idValue.isEmpty()
      || Helpers.isDynamicValue(idValue, getHtmlSourceCode());
  }

  private void reportDuplicateAgainstUnconditionalId(TagNode node, String idValue) {
    Integer firstOccurrenceLine = unconditionalIds.get(idValue);
    if (firstOccurrenceLine != null) {
      createViolation(node, duplicateIdMessage(idValue, firstOccurrenceLine));
    }
  }

  private void registerUnconditionalId(TagNode node, String idValue) {
    Integer firstOccurrenceLine = unconditionalIds.putIfAbsent(idValue, node.getStartLinePosition());
    if (firstOccurrenceLine != null) {
      createViolation(node, duplicateIdMessage(idValue, firstOccurrenceLine));
    }
  }

  private static String duplicateIdMessage(String idValue, int firstOccurrenceLine) {
    return String.format("Duplicate id \"%s\" found. First occurrence was on line %d.",
      idValue, firstOccurrenceLine);
  }
}
