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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import org.sonar.check.Rule;
import org.sonar.plugins.html.api.Helpers;
import org.sonar.plugins.html.api.TemplateConditionalScopeTracker;
import org.sonar.plugins.html.api.TemplateConditionalScopeTracker.ConditionalAttributeScope;
import org.sonar.plugins.html.api.WebFormsRuntimeScopeTracker;
import org.sonar.plugins.html.api.WebFormsRuntimeScopeTracker.ScopeIdentity;
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
 *    IDs inside text-based conditionals are only checked against IDs found outside any conditional block.
 *    Descendant IDs under conditional attributes are checked against IDs outside conditional hosts and against
 *    other conditional paths unless those paths are known to be mutually exclusive.
 * 2. Ignores IDs that contain dynamic/template expressions (e.g., @variable, {{expression}}, ${var})
 *    since these will be unique at runtime.
 * 3. Accounts for generated ASP.NET WebForms client IDs inside repeated naming containers.
 */
@Rule(key = "S7930")
public class NoDuplicateIDCheck extends AbstractPageCheck {

  // IDs seen outside any conditional - these are the "authoritative" IDs
  private final Map<RuntimeId, Integer> unconditionalIds = new HashMap<>();
  private final WebFormsRuntimeScopeTracker webFormsScopeTracker = new WebFormsRuntimeScopeTracker();
  private final Map<RuntimeId, List<ConditionalIdOccurrence>> conditionalIds = new HashMap<>();
  private final TemplateConditionalScopeTracker conditionalScope = new TemplateConditionalScopeTracker();

  @Override
  public void startDocument(List<Node> nodes) {
    unconditionalIds.clear();
    webFormsScopeTracker.reset(nodes, getHtmlSourceCode());
    conditionalIds.clear();
    conditionalScope.reset(Helpers.isRazorFile(getHtmlSourceCode()), nodes);
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
    webFormsScopeTracker.startElement(node);
    handleIdAttribute(node);
  }

  @Override
  public void endElement(TagNode node) {
    conditionalScope.endElement(node);
  }

  private void handleIdAttribute(TagNode node) {
    if (conditionalScope.isInNonRenderedRazorContent()) {
      return;
    }
    String idValue = node.getAttribute("id");
    if (shouldIgnoreId(idValue)) {
      return;
    }
    List<RuntimeId> runtimeIds = runtimeIds(node, idValue);
    if (conditionalScope.isInOpenConditionalScope()) {
      reportDuplicateAgainstUnconditionalId(node, runtimeIds);
      return;
    }

    List<ConditionalAttributeScope> attributeScopes = conditionalScope.conditionalAttributeScopes(node);
    if (!attributeScopes.isEmpty()) {
      if (!reportDuplicateAgainstUnconditionalId(node, runtimeIds)) {
        registerConditionalId(node, runtimeIds, attributeScopes);
      }
    } else if (TemplateConditionalScopeTracker.isConditionalAttributeHost(node)) {
      reportDuplicateAgainstUnconditionalId(node, runtimeIds);
    } else {
      registerUnconditionalId(node, runtimeIds);
    }
  }

  /**
   * Registers an ID found inside conditional-attribute hosts and reports a coexisting duplicate.
   *
   * @param node the element containing the ID
   * @param runtimeIds the runtime IDs derived from the static ID value
   * @param attributeScopes the active conditional-attribute hosts
   */
  private void registerConditionalId(
    TagNode node,
    List<RuntimeId> runtimeIds,
    List<ConditionalAttributeScope> attributeScopes) {
    Integer firstOccurrenceLine = firstCoexistingConditionalOccurrence(runtimeIds, attributeScopes);
    if (firstOccurrenceLine != null) {
      createViolation(node, duplicateIdMessage(runtimeIds.get(0).value(), firstOccurrenceLine));
    }
    for (RuntimeId runtimeId : runtimeIds) {
      conditionalIds.computeIfAbsent(runtimeId, ignored -> new ArrayList<>())
        .add(new ConditionalIdOccurrence(attributeScopes, node.getStartLinePosition()));
    }
  }

  @Nullable
  private Integer firstCoexistingConditionalOccurrence(
    List<RuntimeId> runtimeIds,
    List<ConditionalAttributeScope> attributeScopes) {
    Integer firstOccurrenceLine = null;
    for (RuntimeId runtimeId : runtimeIds) {
      List<ConditionalIdOccurrence> occurrences = conditionalIds.get(runtimeId);
      if (occurrences == null) {
        continue;
      }
      for (ConditionalIdOccurrence occurrence : occurrences) {
        if (canCoexist(attributeScopes, occurrence.attributeScopes())
          && (firstOccurrenceLine == null || occurrence.line() < firstOccurrenceLine)) {
          firstOccurrenceLine = occurrence.line();
        }
      }
    }
    return firstOccurrenceLine;
  }

  /**
   * Determines whether two conditional-host paths might be rendered together.
   *
   * @param firstScopes the first conditional-host path
   * @param secondScopes the second conditional-host path
   * @return false only when the paths diverge through known mutually exclusive hosts
   */
  private static boolean canCoexist(
    List<ConditionalAttributeScope> firstScopes,
    List<ConditionalAttributeScope> secondScopes) {
    int sharedDepth = Math.min(firstScopes.size(), secondScopes.size());
    for (int index = 0; index < sharedDepth; index++) {
      ConditionalAttributeScope firstScope = firstScopes.get(index);
      ConditionalAttributeScope secondScope = secondScopes.get(index);
      if (TemplateConditionalScopeTracker.areMutuallyExclusive(firstScope, secondScope)) {
        return false;
      }
    }
    return true;
  }

  private boolean shouldIgnoreId(@Nullable String idValue) {
    return idValue == null
      || idValue.isEmpty()
      || Helpers.isDynamicValue(idValue, getHtmlSourceCode());
  }

  private boolean reportDuplicateAgainstUnconditionalId(TagNode node, List<RuntimeId> runtimeIds) {
    Integer firstOccurrenceLine = firstOccurrence(runtimeIds);
    if (firstOccurrenceLine != null) {
      createViolation(node, duplicateIdMessage(runtimeIds.get(0).value(), firstOccurrenceLine));
      return true;
    }
    return false;
  }

  private void registerUnconditionalId(TagNode node, List<RuntimeId> runtimeIds) {
    Integer firstOccurrenceLine = firstOccurrence(runtimeIds);
    if (firstOccurrenceLine == null) {
      firstOccurrenceLine = firstConditionalOccurrence(runtimeIds);
    }
    for (RuntimeId runtimeId : runtimeIds) {
      unconditionalIds.putIfAbsent(runtimeId, node.getStartLinePosition());
    }
    if (firstOccurrenceLine != null) {
      createViolation(node, duplicateIdMessage(runtimeIds.get(0).value(), firstOccurrenceLine));
    }
  }

  @Nullable
  private Integer firstOccurrence(List<RuntimeId> runtimeIds) {
    Integer firstOccurrenceLine = null;
    for (RuntimeId runtimeId : runtimeIds) {
      Integer occurrenceLine = unconditionalIds.get(runtimeId);
      if (occurrenceLine != null && (firstOccurrenceLine == null || occurrenceLine < firstOccurrenceLine)) {
        firstOccurrenceLine = occurrenceLine;
      }
    }
    return firstOccurrenceLine;
  }

  @Nullable
  private Integer firstConditionalOccurrence(List<RuntimeId> runtimeIds) {
    Integer firstOccurrenceLine = null;
    for (RuntimeId runtimeId : runtimeIds) {
      List<ConditionalIdOccurrence> occurrences = conditionalIds.get(runtimeId);
      if (occurrences != null && !occurrences.isEmpty()) {
        int occurrenceLine = occurrences.get(0).line();
        if (firstOccurrenceLine == null || occurrenceLine < firstOccurrenceLine) {
          firstOccurrenceLine = occurrenceLine;
        }
      }
    }
    return firstOccurrenceLine;
  }

  private List<RuntimeId> runtimeIds(TagNode node, String idValue) {
    WebFormsRuntimeScopeTracker.Scope scope = webFormsScopeTracker.scope(node);
    ScopeIdentity identity = scope == null ? null : scope.identity();
    Set<String> templateScopes = scope == null ? Set.of() : scope.templateScopes();
    if (templateScopes.isEmpty()) {
      return List.of(new RuntimeId(idValue, identity, null));
    }
    return templateScopes.stream()
      .map(templateScope -> new RuntimeId(idValue, identity, templateScope))
      .toList();
  }

  private static String duplicateIdMessage(String idValue, int firstOccurrenceLine) {
    return String.format("Duplicate id \"%s\" found. First occurrence was on line %d.",
      idValue, firstOccurrenceLine);
  }

  /**
   * Scope key for a duplicate-id lookup. Each naming-container node receives a distinct identity object,
   * independently of {@link TagNode#equals(Object)}.
   */
  private record RuntimeId(String value, @Nullable ScopeIdentity scopeIdentity, @Nullable String templateScope) {
  }

  private record ConditionalIdOccurrence(List<ConditionalAttributeScope> attributeScopes, int line) {
  }
}
