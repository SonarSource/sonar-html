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
import java.util.HashSet;
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

  private static final String DETAILS_VIEW = "detailsview";
  private static final String FORM_VIEW = "formview";

  private static final Set<String> WEBFORMS_NAMING_CONTAINERS = Set.of(
    "gridview", "repeater", DETAILS_VIEW, "listview", FORM_VIEW, "datalist", "datagrid",
    "content", "loginview", "wizard", "createuserwizard", "changepassword", "login", "passwordrecovery");
  private static final Set<String> WEBFORMS_TEMPLATE_NAMING_CONTAINERS = Set.of(
    "gridview", "repeater", DETAILS_VIEW, "listview", FORM_VIEW, "datalist", "datagrid");
  private static final Set<String> WEBFORMS_TEMPLATE_SCOPES = Set.of(
    "itemtemplate", "edititemtemplate", "insertitemtemplate", "alternatingitemtemplate",
    "headertemplate", "footertemplate", "separatortemplate", "emptydatatemplate",
    "emptyitemtemplate", "pagertemplate", "selecteditemtemplate", "grouptemplate",
    "groupseparatortemplate", "itemseparatortemplate", "layouttemplate");
  private static final Set<String> WEBFORMS_EXCLUSIVE_TEMPLATE_SCOPES = Set.of(
    "anonymoustemplate", "loggedintemplate", "changepasswordtemplate", "successtemplate",
    "usernametemplate", "questiontemplate");
  private static final Set<String> WEBFORMS_FORM_MODE_TEMPLATE_SCOPES = Set.of(
    "itemtemplate", "edititemtemplate", "insertitemtemplate");
  private static final Set<String> WEBFORMS_SHARED_FORM_TEMPLATE_SCOPES = Set.of(
    "headertemplate", "footertemplate");
  private static final String WEBFORMS_CONTROLS_NAMESPACE = "System.Web.UI.WebControls";

  // IDs seen outside any conditional - these are the "authoritative" IDs
  private final Map<RuntimeId, Integer> unconditionalIds = new HashMap<>();
  private final Map<TagNode, Integer> webFormsContainerIds = new IdentityHashMap<>();
  private final Set<String> webFormsNamingContainerPrefixes = new HashSet<>();
  private final TemplateConditionalScopeTracker conditionalScope = new TemplateConditionalScopeTracker();
  private int nextWebFormsContainerId;
  @Nullable
  private String pageClientIdMode;

  @Override
  public void startDocument(List<Node> nodes) {
    unconditionalIds.clear();
    webFormsContainerIds.clear();
    webFormsNamingContainerPrefixes.clear();
    webFormsNamingContainerPrefixes.add("asp");
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
    if (!isWebFormsFile()) {
      return;
    }
    if (directiveNode.equalsElementName("Page") || directiveNode.equalsElementName("Control")) {
      pageClientIdMode = directiveNode.getAttribute("clientidmode");
    } else if (directiveNode.equalsElementName("Register")
      && WEBFORMS_CONTROLS_NAMESPACE.equalsIgnoreCase(directiveNode.getAttribute("namespace"))) {
      String tagPrefix = directiveNode.getAttribute("tagprefix");
      if (tagPrefix != null && !tagPrefix.isBlank()) {
        webFormsNamingContainerPrefixes.add(tagPrefix.toLowerCase(Locale.ROOT));
      }
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
    List<RuntimeId> runtimeIds = runtimeIds(node, idValue);
    if (conditionalScope.isInConditional(node)) {
      reportDuplicateAgainstUnconditionalId(node, runtimeIds);
    } else {
      registerUnconditionalId(node, runtimeIds);
    }
  }

  private boolean shouldIgnoreId(@Nullable String idValue) {
    return idValue == null
      || idValue.isEmpty()
      || Helpers.isDynamicValue(idValue, getHtmlSourceCode());
  }

  private void reportDuplicateAgainstUnconditionalId(TagNode node, List<RuntimeId> runtimeIds) {
    Integer firstOccurrenceLine = firstOccurrence(runtimeIds);
    if (firstOccurrenceLine != null) {
      createViolation(node, duplicateIdMessage(runtimeIds.get(0).value(), firstOccurrenceLine));
    }
  }

  private void registerUnconditionalId(TagNode node, List<RuntimeId> runtimeIds) {
    Integer firstOccurrenceLine = firstOccurrence(runtimeIds);
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

  private List<RuntimeId> runtimeIds(TagNode node, String idValue) {
    WebFormsScope scope = webFormsScope(node);
    if (scope == null) {
      return List.of(new RuntimeId(idValue, 0, null));
    }
    int containerId = webFormsContainerIds.computeIfAbsent(scope.namingContainer(), key -> nextWebFormsContainerId++);
    if (scope.templateKind() == null) {
      return List.of(new RuntimeId(idValue, containerId, null));
    }
    return templateScopes(scope.containerName(), scope.templateKind()).stream()
      .map(templateKind -> new RuntimeId(idValue, containerId, templateKind))
      .toList();
  }

  @Nullable
  private WebFormsScope webFormsScope(TagNode node) {
    if (!isWebFormsFile() || !isServerControl(node) || !hasGeneratedClientId(node)) {
      return null;
    }

    String templateKind = null;
    String exclusiveTemplateKind = null;
    TagNode ancestor = node.getParent();
    while (ancestor != null) {
      String localName = ancestor.getLocalName().toLowerCase(Locale.ROOT);
      if (templateKind == null && WEBFORMS_TEMPLATE_SCOPES.contains(localName)) {
        templateKind = localName;
      }
      if (exclusiveTemplateKind == null && WEBFORMS_EXCLUSIVE_TEMPLATE_SCOPES.contains(localName)) {
        exclusiveTemplateKind = localName;
      }
      if (isWebFormsNamingContainer(ancestor)) {
        String scopedTemplateKind = WEBFORMS_TEMPLATE_NAMING_CONTAINERS.contains(localName) ? templateKind : exclusiveTemplateKind;
        return new WebFormsScope(ancestor, localName, scopedTemplateKind);
      }
      ancestor = ancestor.getParent();
    }
    return null;
  }

  private static Set<String> templateScopes(String containerName, String templateKind) {
    if ((DETAILS_VIEW.equals(containerName) || FORM_VIEW.equals(containerName))
      && WEBFORMS_SHARED_FORM_TEMPLATE_SCOPES.contains(templateKind)) {
      return WEBFORMS_FORM_MODE_TEMPLATE_SCOPES;
    }
    return Set.of(templateKind);
  }

  private boolean isWebFormsFile() {
    String filename = getHtmlSourceCode().inputFile().filename().toLowerCase(Locale.ROOT);
    return filename.endsWith(".aspx") || filename.endsWith(".ascx");
  }

  private static boolean isServerControl(TagNode node) {
    return "server".equalsIgnoreCase(node.getAttribute("runat"));
  }

  private boolean isWebFormsNamingContainer(TagNode node) {
    String nodeName = node.getNodeName();
    if (!nodeName.contains(":") || nodeName.startsWith(":")) {
      return false;
    }
    String tagPrefix = nodeName.substring(0, nodeName.indexOf(':')).toLowerCase(Locale.ROOT);
    return webFormsNamingContainerPrefixes.contains(tagPrefix)
      && WEBFORMS_NAMING_CONTAINERS.contains(node.getLocalName().toLowerCase(Locale.ROOT))
      && isServerControl(node);
  }

  private boolean hasGeneratedClientId(TagNode node) {
    TagNode control = node;
    while (control != null) {
      String clientIdMode = control.getAttribute("clientidmode");
      if (clientIdMode != null && !"inherit".equalsIgnoreCase(clientIdMode)) {
        return "autoid".equalsIgnoreCase(clientIdMode) || "predictable".equalsIgnoreCase(clientIdMode);
      }
      control = nearestWebFormsNamingContainer(control.getParent());
    }
    return pageClientIdMode == null
      || "inherit".equalsIgnoreCase(pageClientIdMode)
      || "autoid".equalsIgnoreCase(pageClientIdMode)
      || "predictable".equalsIgnoreCase(pageClientIdMode);
  }

  @Nullable
  private TagNode nearestWebFormsNamingContainer(@Nullable TagNode node) {
    TagNode ancestor = node;
    while (ancestor != null) {
      if (isWebFormsNamingContainer(ancestor)) {
        return ancestor;
      }
      ancestor = ancestor.getParent();
    }
    return null;
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

  private record WebFormsScope(TagNode namingContainer, String containerName, @Nullable String templateKind) {
  }
}
