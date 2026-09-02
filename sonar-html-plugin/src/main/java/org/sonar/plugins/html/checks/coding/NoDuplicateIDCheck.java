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

  private static final Set<String> WEBFORMS_TEMPLATE_NAMING_CONTAINERS = Set.of(
    "gridview", "repeater", DETAILS_VIEW, "listview", FORM_VIEW, "datalist", "datagrid", "menu", "sitemappath");
  private static final Set<String> WEBFORMS_WIZARD_NAMING_CONTAINERS = Set.of("wizard", "createuserwizard");
  private static final Set<String> WEBFORMS_NAMING_CONTAINERS = union(
    WEBFORMS_TEMPLATE_NAMING_CONTAINERS,
    WEBFORMS_WIZARD_NAMING_CONTAINERS,
    Set.of("content", "loginview", "changepassword", "login", "passwordrecovery"));
  private static final Set<String> WEBFORMS_TEMPLATE_SCOPES = Set.of(
    "itemtemplate", "edititemtemplate", "insertitemtemplate", "alternatingitemtemplate",
    "headertemplate", "footertemplate", "separatortemplate", "emptydatatemplate",
    "emptyitemtemplate", "pagertemplate", "selecteditemtemplate", "grouptemplate",
    "groupseparatortemplate", "itemseparatortemplate", "layouttemplate",
    "staticitemtemplate", "dynamicitemtemplate", "currentnodetemplate", "nodetemplate",
    "rootnodetemplate", "pathseparatortemplate");
  private static final Set<String> WEBFORMS_EXCLUSIVE_TEMPLATE_SCOPES = Set.of(
    "anonymoustemplate", "loggedintemplate", "changepasswordtemplate", "successtemplate",
    "usernametemplate", "questiontemplate");
  private static final Set<String> WEBFORMS_WIZARD_STEP_SCOPES = Set.of(
    "wizardstep", "templatedwizardstep", "createuserwizardstep", "completewizardstep");
  private static final Set<String> WEBFORMS_FORM_MODE_TEMPLATE_SCOPES = Set.of(
    "itemtemplate", "edititemtemplate", "insertitemtemplate");
  // Pager rows implement INonBindingContainer and therefore have a distinct naming scope.
  private static final Set<String> WEBFORMS_SHARED_FORM_TEMPLATE_SCOPES = Set.of(
    "headertemplate", "footertemplate");
  private static final String WEBFORMS_CONTROLS_NAMESPACE = "System.Web.UI.WebControls";

  // IDs seen outside any conditional - these are the "authoritative" IDs
  private final Map<RuntimeId, Integer> unconditionalIds = new HashMap<>();
  private final Map<TagNode, Integer> webFormsScopeIds = new IdentityHashMap<>();
  private final Set<String> webFormsNamingContainerPrefixes = new HashSet<>();
  private final TemplateConditionalScopeTracker conditionalScope = new TemplateConditionalScopeTracker();
  private int nextWebFormsScopeId;
  @Nullable
  private String pageClientIdMode;

  @Override
  public void startDocument(List<Node> nodes) {
    unconditionalIds.clear();
    webFormsScopeIds.clear();
    webFormsNamingContainerPrefixes.clear();
    webFormsNamingContainerPrefixes.add("asp");
    nextWebFormsScopeId = 1;
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
    if (!Helpers.isWebFormsFile(getHtmlSourceCode())) {
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
    int containerId = scopeId(scope.namingContainer());
    if (scope.templateScopes().isEmpty()) {
      return List.of(new RuntimeId(idValue, containerId, null));
    }
    return scope.templateScopes().stream()
      .map(templateScope -> new RuntimeId(idValue, containerId, templateScope))
      .toList();
  }

  @Nullable
  private WebFormsScope webFormsScope(TagNode node) {
    if (!Helpers.isWebFormsFile(getHtmlSourceCode()) || !isServerControl(node)) {
      return null;
    }

    WebFormsTraversal traversal = new WebFormsTraversal(node);
    TagNode ancestor = node.getParent();
    while (ancestor != null && traversal.shouldContinue()) {
      traversal.visit(ancestor);
      ancestor = ancestor.getParent();
    }

    if (traversal.namingContainer == null || !hasGeneratedClientId(traversal.generatedClientId)) {
      return null;
    }
    return new WebFormsScope(
      traversal.namingContainer,
      webFormsTemplateScopes(
        traversal.namingContainer,
        traversal.containerName,
        traversal.templateKind,
        traversal.exclusiveTemplateKind,
        traversal.wizardStep));
  }

  private Set<String> webFormsTemplateScopes(
    TagNode namingContainer,
    String containerName,
    @Nullable String templateKind,
    @Nullable String exclusiveTemplateKind,
    @Nullable TagNode wizardStep) {
    if (WEBFORMS_TEMPLATE_NAMING_CONTAINERS.contains(containerName) && templateKind != null) {
      return templateScopes(containerName, templateKind);
    }
    if (WEBFORMS_WIZARD_NAMING_CONTAINERS.contains(containerName)) {
      return wizardTemplateScopes(namingContainer, wizardStep);
    }
    return exclusiveTemplateKind == null ? Set.of() : Set.of(exclusiveTemplateKind);
  }

  private static Set<String> templateScopes(String containerName, String templateKind) {
    if ((DETAILS_VIEW.equals(containerName) || FORM_VIEW.equals(containerName))
      && WEBFORMS_SHARED_FORM_TEMPLATE_SCOPES.contains(templateKind)) {
      return WEBFORMS_FORM_MODE_TEMPLATE_SCOPES;
    }
    return Set.of(templateKind);
  }

  private Set<String> wizardTemplateScopes(TagNode namingContainer, @Nullable TagNode wizardStep) {
    if (wizardStep != null) {
      return Set.of(wizardStepScope(wizardStep));
    }
    Set<String> scopes = new HashSet<>();
    collectWizardStepScopes(namingContainer, scopes);
    return scopes;
  }

  private void collectWizardStepScopes(TagNode node, Set<String> scopes) {
    for (TagNode child : node.getChildren()) {
      String localName = child.getLocalName().toLowerCase(Locale.ROOT);
      if (isWebFormsWizardStep(child, localName)) {
        scopes.add(wizardStepScope(child));
      } else if (!isWebFormsWizard(child, localName)) {
        collectWizardStepScopes(child, scopes);
      }
    }
  }

  private String wizardStepScope(TagNode wizardStep) {
    return "wizard-step-" + scopeId(wizardStep);
  }

  private int scopeId(TagNode scope) {
    return webFormsScopeIds.computeIfAbsent(scope, key -> nextWebFormsScopeId++);
  }

  private static boolean isServerControl(TagNode node) {
    return "server".equalsIgnoreCase(node.getAttribute("runat"));
  }

  private boolean isWebFormsNamingContainer(TagNode node) {
    return WEBFORMS_NAMING_CONTAINERS.contains(node.getLocalName().toLowerCase(Locale.ROOT))
      && isKnownWebFormsControl(node);
  }

  private boolean isWebFormsWizardStep(TagNode node, String localName) {
    return WEBFORMS_WIZARD_STEP_SCOPES.contains(localName) && isKnownWebFormsControl(node);
  }

  private boolean isWebFormsWizard(TagNode node, String localName) {
    return WEBFORMS_WIZARD_NAMING_CONTAINERS.contains(localName) && isKnownWebFormsControl(node);
  }

  private boolean isKnownWebFormsControl(TagNode node) {
    String nodeName = node.getNodeName();
    int prefixEnd = nodeName.indexOf(':');
    if (prefixEnd <= 0 || !isServerControl(node)) {
      return false;
    }
    String tagPrefix = nodeName.substring(0, prefixEnd).toLowerCase(Locale.ROOT);
    return webFormsNamingContainerPrefixes.contains(tagPrefix);
  }

  private boolean hasGeneratedClientId(@Nullable Boolean generatedClientId) {
    if (generatedClientId != null) {
      return generatedClientId;
    }
    Boolean pageGeneratedClientId = usesGeneratedClientId(pageClientIdMode);
    return pageGeneratedClientId == null || pageGeneratedClientId;
  }

  @Nullable
  private static Boolean usesGeneratedClientId(@Nullable String clientIdMode) {
    if (clientIdMode == null || "inherit".equalsIgnoreCase(clientIdMode)) {
      return null;
    }
    return "autoid".equalsIgnoreCase(clientIdMode) || "predictable".equalsIgnoreCase(clientIdMode);
  }

  private static Set<String> union(Set<String> first, Set<String> second, Set<String> third) {
    Set<String> result = new HashSet<>(first);
    result.addAll(second);
    result.addAll(third);
    return Set.copyOf(result);
  }

  private static String duplicateIdMessage(String idValue, int firstOccurrenceLine) {
    return String.format("Duplicate id \"%s\" found. First occurrence was on line %d.",
      idValue, firstOccurrenceLine);
  }

  private final class WebFormsTraversal {
    @Nullable
    private Boolean generatedClientId;
    @Nullable
    private TagNode namingContainer;
    private String containerName = "";
    @Nullable
    private String templateKind;
    @Nullable
    private String exclusiveTemplateKind;
    @Nullable
    private TagNode wizardStep;

    private WebFormsTraversal(TagNode node) {
      generatedClientId = usesGeneratedClientId(node.getAttribute("clientidmode"));
    }

    private boolean shouldContinue() {
      return namingContainer == null || generatedClientId == null;
    }

    private void visit(TagNode ancestor) {
      String localName = ancestor.getLocalName().toLowerCase(Locale.ROOT);
      if (namingContainer == null) {
        captureTemplateScopes(ancestor, localName);
      }
      if (isWebFormsNamingContainer(ancestor)) {
        captureNamingContainer(ancestor, localName);
      }
    }

    private void captureTemplateScopes(TagNode ancestor, String localName) {
      if (templateKind == null && WEBFORMS_TEMPLATE_SCOPES.contains(localName)) {
        templateKind = localName;
      }
      if (exclusiveTemplateKind == null && WEBFORMS_EXCLUSIVE_TEMPLATE_SCOPES.contains(localName)) {
        exclusiveTemplateKind = localName;
      }
      if (wizardStep == null && isWebFormsWizardStep(ancestor, localName)) {
        wizardStep = ancestor;
      }
    }

    private void captureNamingContainer(TagNode ancestor, String localName) {
      if (namingContainer == null) {
        namingContainer = ancestor;
        containerName = localName;
      }
      if (generatedClientId == null) {
        generatedClientId = usesGeneratedClientId(ancestor.getAttribute("clientidmode"));
      }
    }
  }

  /**
   * Scope key for a duplicate-id lookup. Naming containers and wizard steps receive stable IDs
   * per distinct {@link TagNode} through an {@link IdentityHashMap}; zero represents the page-global scope.
   */
  private record RuntimeId(String value, int containerId, @Nullable String templateScope) {
  }

  private record WebFormsScope(TagNode namingContainer, Set<String> templateScopes) {
  }
}
