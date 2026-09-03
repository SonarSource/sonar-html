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
package org.sonar.plugins.html.api;

import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import org.sonar.plugins.html.node.DirectiveNode;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.NodeType;
import org.sonar.plugins.html.node.TagNode;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

/** Tracks the runtime ID scope of ASP.NET WebForms server controls. */
public final class WebFormsRuntimeScopeTracker {

  private static final String DETAILS_VIEW = "detailsview";
  private static final String FORM_VIEW = "formview";
  private static final String CLIENT_ID_MODE_ATTRIBUTE = "clientidmode";
  private static final String WEBFORMS_CONTROLS_NAMESPACE = "System.Web.UI.WebControls";

  private static final Set<String> TEMPLATE_NAMING_CONTAINERS = Set.of(
    "gridview", "repeater", DETAILS_VIEW, "listview", FORM_VIEW, "datalist", "datagrid", "menu", "sitemappath");
  private static final Set<String> WIZARD_NAMING_CONTAINERS = Set.of("wizard", "createuserwizard");
  private static final Set<String> NAMING_CONTAINERS = union(
    TEMPLATE_NAMING_CONTAINERS,
    WIZARD_NAMING_CONTAINERS,
    Set.of("content", "loginview", "changepassword", "login", "passwordrecovery"));
  private static final Set<String> TEMPLATE_SCOPES = Set.of(
    "itemtemplate", "edititemtemplate", "insertitemtemplate", "alternatingitemtemplate",
    "headertemplate", "footertemplate", "separatortemplate", "emptydatatemplate",
    "emptyitemtemplate", "pagertemplate", "selecteditemtemplate", "grouptemplate",
    "groupseparatortemplate", "itemseparatortemplate", "layouttemplate",
    "staticitemtemplate", "dynamicitemtemplate", "currentnodetemplate", "nodetemplate",
    "rootnodetemplate", "pathseparatortemplate");
  private static final Set<String> EXCLUSIVE_TEMPLATE_SCOPES = Set.of(
    "anonymoustemplate", "loggedintemplate", "changepasswordtemplate", "successtemplate",
    "usernametemplate", "questiontemplate");
  private static final Set<String> WIZARD_STEP_SCOPES = Set.of(
    "wizardstep", "templatedwizardstep", "createuserwizardstep", "completewizardstep");
  private static final Set<String> FORM_MODE_TEMPLATE_SCOPES = Set.of(
    "itemtemplate", "edititemtemplate", "insertitemtemplate");
  // Pager rows implement INonBindingContainer and therefore have a distinct naming scope.
  private static final Set<String> SHARED_FORM_TEMPLATE_SCOPES = Set.of(
    "headertemplate", "footertemplate");

  private final Map<TagNode, NodeContext> contexts = new IdentityHashMap<>();
  private final Map<TagNode, Scope> scopes = new IdentityHashMap<>();
  private final Map<TagNode, Set<String>> wizardScopes = new IdentityHashMap<>();
  private final Map<TagNode, String> wizardStepScopes = new IdentityHashMap<>();
  private final Set<String> namingContainerPrefixes = new HashSet<>();
  private boolean isWebFormsFile;
  private boolean pageGeneratedClientId;
  private int nextWizardStepId;

  public void reset(List<Node> nodes, HtmlSourceCode sourceCode) {
    contexts.clear();
    scopes.clear();
    wizardScopes.clear();
    wizardStepScopes.clear();
    namingContainerPrefixes.clear();
    namingContainerPrefixes.add("asp");
    nextWizardStepId = 1;
    isWebFormsFile = Helpers.isWebFormsFile(sourceCode);
    pageGeneratedClientId = true;
    if (isWebFormsFile) {
      collectDirectives(nodes);
    }
  }

  public void startElement(TagNode node) {
    if (!isWebFormsFile) {
      return;
    }

    NodeContext parentContext = context(node.getParent());
    Boolean nodeMode = usesGeneratedClientId(node.getAttribute(CLIENT_ID_MODE_ATTRIBUTE));
    boolean nodeGeneratedClientId = nodeMode == null ? parentContext.generatedClientId() : nodeMode;
    if (isServerControl(node) && parentContext.namingContainer() != null && nodeGeneratedClientId) {
      scopes.put(node, new Scope(
        parentContext.scopeIdentity(),
        templateScopes(parentContext)));
    }

    String localName = node.getLocalName().toLowerCase(Locale.ROOT);
    if (isNamingContainer(node, localName)) {
      contexts.put(node, new NodeContext(
        node,
        new ScopeIdentity(),
        localName,
        nodeGeneratedClientId,
        null,
        null,
        null));
      return;
    }

    contexts.put(node, new NodeContext(
      parentContext.namingContainer(),
      parentContext.scopeIdentity(),
      parentContext.containerName(),
      parentContext.generatedClientId(),
      nearestMatching(localName, TEMPLATE_SCOPES, parentContext.templateKind()),
      nearestMatching(localName, EXCLUSIVE_TEMPLATE_SCOPES, parentContext.exclusiveTemplateKind()),
      wizardStep(node, localName, parentContext.wizardStep())));
  }

  @Nullable
  public Scope scope(TagNode node) {
    return scopes.get(node);
  }

  private void collectDirectives(List<Node> nodes) {
    for (Node node : nodes) {
      if (node.getNodeType() != NodeType.DIRECTIVE) {
        continue;
      }
      DirectiveNode directive = (DirectiveNode) node;
      if (directive.equalsElementName("Page") || directive.equalsElementName("Control")) {
        Boolean directiveMode = usesGeneratedClientId(directive.getAttribute(CLIENT_ID_MODE_ATTRIBUTE));
        pageGeneratedClientId = directiveMode == null || directiveMode;
      } else if (directive.equalsElementName("Register")
        && WEBFORMS_CONTROLS_NAMESPACE.equalsIgnoreCase(directive.getAttribute("namespace"))) {
        String tagPrefix = directive.getAttribute("tagprefix");
        if (tagPrefix != null && !tagPrefix.isBlank()) {
          namingContainerPrefixes.add(tagPrefix.toLowerCase(Locale.ROOT));
        }
      }
    }
  }

  private NodeContext context(@Nullable TagNode node) {
    if (node == null) {
      return rootContext();
    }
    return contexts.getOrDefault(node, rootContext());
  }

  private NodeContext rootContext() {
    return new NodeContext(null, null, "", pageGeneratedClientId, null, null, null);
  }

  private Set<String> templateScopes(NodeContext context) {
    if (TEMPLATE_NAMING_CONTAINERS.contains(context.containerName()) && context.templateKind() != null) {
      return dataTemplateScopes(context.containerName(), context.templateKind());
    }
    if (WIZARD_NAMING_CONTAINERS.contains(context.containerName())) {
      if (context.wizardStep() != null) {
        return Set.of(wizardStepScope(context.wizardStep()));
      }
      return wizardScopes.computeIfAbsent(context.namingContainer(), this::collectWizardStepScopes);
    }
    return context.exclusiveTemplateKind() == null ? Set.of() : Set.of(context.exclusiveTemplateKind());
  }

  private static Set<String> dataTemplateScopes(String containerName, String templateKind) {
    if ((DETAILS_VIEW.equals(containerName) || FORM_VIEW.equals(containerName))
      && SHARED_FORM_TEMPLATE_SCOPES.contains(templateKind)) {
      return FORM_MODE_TEMPLATE_SCOPES;
    }
    return Set.of(templateKind);
  }

  private Set<String> collectWizardStepScopes(TagNode namingContainer) {
    Set<String> result = new HashSet<>();
    collectWizardStepScopes(namingContainer, result);
    return Set.copyOf(result);
  }

  private void collectWizardStepScopes(TagNode node, Set<String> result) {
    for (TagNode child : node.getChildren()) {
      String localName = child.getLocalName().toLowerCase(Locale.ROOT);
      if (isWizardStep(child, localName)) {
        result.add(wizardStepScope(child));
      } else if (!isWizard(child, localName)) {
        collectWizardStepScopes(child, result);
      }
    }
  }

  private String wizardStepScope(TagNode wizardStep) {
    return wizardStepScopes.computeIfAbsent(wizardStep, key -> "wizard-step-" + nextWizardStepId++);
  }

  @Nullable
  private TagNode wizardStep(TagNode node, String localName, @Nullable TagNode inheritedWizardStep) {
    return inheritedWizardStep == null && isWizardStep(node, localName) ? node : inheritedWizardStep;
  }

  private boolean isNamingContainer(TagNode node, String localName) {
    return NAMING_CONTAINERS.contains(localName) && isKnownControl(node);
  }

  private boolean isWizard(TagNode node, String localName) {
    return WIZARD_NAMING_CONTAINERS.contains(localName) && isKnownControl(node);
  }

  private boolean isWizardStep(TagNode node, String localName) {
    return WIZARD_STEP_SCOPES.contains(localName) && hasKnownPrefix(node);
  }

  private boolean isKnownControl(TagNode node) {
    return hasKnownPrefix(node) && isServerControl(node);
  }

  private boolean hasKnownPrefix(TagNode node) {
    String nodeName = node.getNodeName();
    int prefixEnd = nodeName.indexOf(':');
    if (prefixEnd <= 0) {
      return false;
    }
    String tagPrefix = nodeName.substring(0, prefixEnd).toLowerCase(Locale.ROOT);
    // Project-wide web.config registrations are unavailable during a file-level check. Unknown
    // prefixes stay conservative because their runtime type may not implement INamingContainer.
    return namingContainerPrefixes.contains(tagPrefix);
  }

  private static boolean isServerControl(TagNode node) {
    return "server".equalsIgnoreCase(node.getAttribute("runat"));
  }

  @Nullable
  private static Boolean usesGeneratedClientId(@Nullable String clientIdMode) {
    if (clientIdMode == null || "inherit".equalsIgnoreCase(clientIdMode)) {
      return null;
    }
    return "autoid".equalsIgnoreCase(clientIdMode) || "predictable".equalsIgnoreCase(clientIdMode);
  }

  @Nullable
  private static String nearestMatching(String localName, Set<String> candidates, @Nullable String current) {
    return candidates.contains(localName) ? localName : current;
  }

  private static Set<String> union(Set<String> first, Set<String> second, Set<String> third) {
    Set<String> result = new HashSet<>(first);
    result.addAll(second);
    result.addAll(third);
    return Set.copyOf(result);
  }

  public record Scope(ScopeIdentity identity, Set<String> templateScopes) {
  }

  /** An equality key that intentionally identifies one runtime naming-container instance. */
  public static final class ScopeIdentity {
    private ScopeIdentity() {
    }
  }

  private record NodeContext(
    @Nullable TagNode namingContainer,
    @Nullable ScopeIdentity scopeIdentity,
    String containerName,
    boolean generatedClientId,
    @Nullable String templateKind,
    @Nullable String exclusiveTemplateKind,
    @Nullable TagNode wizardStep) {
  }
}
