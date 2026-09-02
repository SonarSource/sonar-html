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
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
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
 * 3. Accounts for generated ASP.NET WebForms client IDs inside repeated naming containers.
 */
@Rule(key = "S7930")
public class NoDuplicateIDCheck extends AbstractPageCheck {

  private static final Set<String> WEBFORMS_NAMING_CONTAINERS = Set.of(
    "gridview", "repeater", "detailsview", "listview", "formview", "datalist", "datagrid",
    "content", "contentplaceholder");
  private static final Set<String> WEBFORMS_TEMPLATE_SCOPES = Set.of(
    "itemtemplate", "edititemtemplate", "insertitemtemplate", "alternatingitemtemplate",
    "headertemplate", "footertemplate", "separatortemplate", "emptydatatemplate",
    "emptyitemtemplate", "pagertemplate", "selecteditemtemplate", "grouptemplate",
    "groupseparatortemplate", "itemseparatortemplate", "layouttemplate");

  // IDs seen outside any conditional - these are the "authoritative" IDs
  private final Map<RuntimeId, Integer> unconditionalIds = new HashMap<>();
  private final Map<TagNode, Integer> webFormsContainerIds = new IdentityHashMap<>();
  private final TemplateConditionalScopeTracker conditionalScope = new TemplateConditionalScopeTracker();
  private int nextWebFormsContainerId;
  @Nullable
  private String pageClientIdMode;

  @Override
  public void startDocument(List<Node> nodes) {
    unconditionalIds.clear();
    webFormsContainerIds.clear();
    nextWebFormsContainerId = 1;
    conditionalScope.reset(Helpers.isRazorFile(getHtmlSourceCode()));
    pageClientIdMode = null;
  }

  @Override
  public void characters(TextNode textNode) {
    conditionalScope.visitText(textNode);
  }

  @Override
  public void directive(DirectiveNode directiveNode) {
    conditionalScope.visitDirective(directiveNode);
    if (isWebFormsFile() && (directiveNode.equalsElementName("Page") || directiveNode.equalsElementName("Control"))) {
      pageClientIdMode = directiveNode.getAttribute("clientidmode");
    }
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
    if (conditionalScope.isInNonRenderedRazorContent()) {
      return;
    }
    String idValue = node.getAttribute("id");
    if (shouldIgnoreId(idValue)) {
      return;
    }
    RuntimeId runtimeId = runtimeId(node, idValue);
    if (conditionalScope.isInConditional(node)) {
      reportDuplicateAgainstUnconditionalId(node, runtimeId);
    } else {
      registerUnconditionalId(node, runtimeId);
    }
  }

  private boolean shouldIgnoreId(@Nullable String idValue) {
    return idValue == null
      || idValue.isEmpty()
      || Helpers.isDynamicValue(idValue, getHtmlSourceCode());
  }

  private void reportDuplicateAgainstUnconditionalId(TagNode node, RuntimeId runtimeId) {
    Integer firstOccurrenceLine = unconditionalIds.get(runtimeId);
    if (firstOccurrenceLine != null) {
      createViolation(node, duplicateIdMessage(runtimeId.value(), firstOccurrenceLine));
    }
  }

  private void registerUnconditionalId(TagNode node, RuntimeId runtimeId) {
    Integer firstOccurrenceLine = unconditionalIds.putIfAbsent(runtimeId, node.getStartLinePosition());
    if (firstOccurrenceLine != null) {
      createViolation(node, duplicateIdMessage(runtimeId.value(), firstOccurrenceLine));
    }
  }

  private RuntimeId runtimeId(TagNode node, String idValue) {
    WebFormsScope scope = webFormsScope(node);
    if (scope == null) {
      return new RuntimeId(idValue, 0, null);
    }
    int containerId = webFormsContainerIds.computeIfAbsent(scope.namingContainer(), key -> nextWebFormsContainerId++);
    return new RuntimeId(idValue, containerId, scope.templateKind());
  }

  @Nullable
  private WebFormsScope webFormsScope(TagNode node) {
    if (!isWebFormsFile() || !isServerControl(node) || !hasGeneratedClientId(node)) {
      return null;
    }

    String templateKind = null;
    TagNode ancestor = node.getParent();
    while (ancestor != null) {
      String localName = ancestor.getLocalName().toLowerCase(Locale.ROOT);
      if (templateKind == null && WEBFORMS_TEMPLATE_SCOPES.contains(localName)) {
        templateKind = localName;
      }
      if (WEBFORMS_NAMING_CONTAINERS.contains(localName) && isServerControl(ancestor)) {
        return new WebFormsScope(ancestor, templateKind);
      }
      ancestor = ancestor.getParent();
    }
    return null;
  }

  private boolean isWebFormsFile() {
    String filename = getHtmlSourceCode().inputFile().filename().toLowerCase(Locale.ROOT);
    return filename.endsWith(".aspx") || filename.endsWith(".ascx");
  }

  private static boolean isServerControl(TagNode node) {
    return "server".equalsIgnoreCase(node.getAttribute("runat"));
  }

  private boolean hasGeneratedClientId(TagNode node) {
    TagNode control = node;
    while (control != null) {
      if (isServerControl(control)) {
        String clientIdMode = control.getAttribute("clientidmode");
        if (clientIdMode != null && !"inherit".equalsIgnoreCase(clientIdMode)) {
          return "autoid".equalsIgnoreCase(clientIdMode) || "predictable".equalsIgnoreCase(clientIdMode);
        }
      }
      control = control.getParent();
    }
    return pageClientIdMode == null
      || "inherit".equalsIgnoreCase(pageClientIdMode)
      || "autoid".equalsIgnoreCase(pageClientIdMode)
      || "predictable".equalsIgnoreCase(pageClientIdMode);
  }

  private static String duplicateIdMessage(String idValue, int firstOccurrenceLine) {
    return String.format("Duplicate id \"%s\" found. First occurrence was on line %d.",
      idValue, firstOccurrenceLine);
  }

  /**
   * Scope key for a duplicate-id lookup. A positive {@code containerId} is assigned per distinct
   * {@link TagNode} through an {@link IdentityHashMap}; zero represents the page-global scope.
   */
  private record RuntimeId(String value, int containerId, @Nullable String templateKind) {
  }

  private record WebFormsScope(TagNode namingContainer, @Nullable String templateKind) {
  }
}
