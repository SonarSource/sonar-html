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

  // Curated built-in controls whose runtime naming-container behavior is known. Register directives
  // provide the extension point for standard controls under another prefix and for .ascx user controls.
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
  // Every navigation template is instantiated in a BaseNavigationTemplateContainer of its own, and
  // CustomNavigationTemplate gets one such container per step, so each renders in a distinct naming
  // scope. HeaderTemplate and SideBarTemplate are deliberately excluded: their containers are naming
  // containers only in the default table rendering, and a LayoutTemplate instantiates them in a
  // plain Control that shares the wizard scope.
  private static final Set<String> WIZARD_NAVIGATION_TEMPLATE_SCOPES = Set.of(
    "startnavigationtemplate", "stepnavigationtemplate", "finishnavigationtemplate",
    "customnavigationtemplate");
  private static final Set<String> FORM_MODE_TEMPLATE_SCOPES = Set.of(
    "itemtemplate", "edititemtemplate", "insertitemtemplate");
  // Header and footer render alongside each active form mode. PagerTemplate is deliberately
  // excluded because pager rows implement INonBindingContainer and have a distinct naming scope.
  private static final Set<String> SHARED_FORM_TEMPLATE_SCOPES = Set.of(
    "headertemplate", "footertemplate");

  private final Map<TagNode, NodeContext> contexts = new IdentityHashMap<>();
  private final Map<TagNode, Scope> scopes = new IdentityHashMap<>();
  private final Map<TagNode, Set<String>> wizardScopes = new IdentityHashMap<>();
  private final Map<TagNode, String> wizardStepScopes = new IdentityHashMap<>();
  private final Set<String> namingContainerPrefixes = new HashSet<>();
  private final Set<String> registeredUserControls = new HashSet<>();
  private boolean isWebFormsFile;
  private boolean pageGeneratedClientId;
  private int nextWizardStepId;

  public void reset(List<Node> nodes, HtmlSourceCode sourceCode) {
    contexts.clear();
    scopes.clear();
    wizardScopes.clear();
    wizardStepScopes.clear();
    namingContainerPrefixes.clear();
    registeredUserControls.clear();
    // "asp" is the built-in prefix for System.Web.UI.WebControls.
    namingContainerPrefixes.add("asp");
    nextWizardStepId = 1;
    isWebFormsFile = Helpers.isWebFormsFile(sourceCode);
    // Without a Page or Control directive, the framework default of generated client IDs applies.
    // A site-wide <pages clientIDMode="Static" /> in web.config would override it, but project
    // configuration is out of reach of a file-level check, so that case stays unreported.
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
    if (Helpers.isServerControl(node) && parentContext.namingContainer() != null && nodeGeneratedClientId) {
      scopes.put(node, new Scope(
        parentContext.scopeIdentity(),
        templateScopes(parentContext)));
    }

    String localName = node.getLocalName().toLowerCase(Locale.ROOT);
    if (isNamingContainer(node, localName)) {
      boolean registeredUserControl = isRegisteredUserControl(node);
      contexts.put(node, new NodeContext(
        node,
        new ScopeIdentity(),
        // User controls do not adopt built-in template semantics even when their TagName matches
        // a standard control such as Repeater.
        registeredUserControl ? "" : localName,
        nodeGeneratedClientId,
        null,
        null,
        WizardContext.NONE));
      return;
    }

    contexts.put(node, new NodeContext(
      parentContext.namingContainer(),
      parentContext.scopeIdentity(),
      parentContext.containerName(),
      // ClientIDMode is inherited from the nearest naming container, not from arbitrary
      // intervening controls such as Panel.
      parentContext.generatedClientId(),
      nearestMatching(localName, TEMPLATE_SCOPES, parentContext.templateKind()),
      nearestMatching(localName, EXCLUSIVE_TEMPLATE_SCOPES, parentContext.exclusiveTemplateKind()),
      wizardContext(node, localName, parentContext.wizard())));
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
      } else if (directive.equalsElementName("Register")) {
        collectRegistration(directive);
      }
    }
  }

  private void collectRegistration(DirectiveNode directive) {
    String tagPrefix = directive.getAttribute("tagprefix");
    if (tagPrefix == null || tagPrefix.isBlank()) {
      return;
    }
    String normalizedPrefix = tagPrefix.toLowerCase(Locale.ROOT);
    String namespace = directive.getAttribute("namespace");
    if (namespace != null && !namespace.isBlank()) {
      // A prefix can be registered for several namespaces. In particular, the built-in "asp"
      // prefix already maps to both System.Web.UI and System.Web.UI.WebControls, so an additional
      // registration must not hide the built-in controls.
      if (WEBFORMS_CONTROLS_NAMESPACE.equalsIgnoreCase(namespace)) {
        namingContainerPrefixes.add(normalizedPrefix);
      }
      return;
    }
    String tagName = directive.getAttribute("tagname");
    String source = directive.getAttribute("src");
    if (tagName != null && !tagName.isBlank() && source != null && !source.isBlank()) {
      registeredUserControls.add(normalizedPrefix + ":" + tagName.toLowerCase(Locale.ROOT));
    }
  }

  private NodeContext context(@Nullable TagNode node) {
    if (node == null) {
      return rootContext();
    }
    NodeContext context = contexts.get(node);
    return context == null ? rootContext() : context;
  }

  private NodeContext rootContext() {
    return new NodeContext(null, null, "", pageGeneratedClientId, null, null, WizardContext.NONE);
  }

  private Set<String> templateScopes(NodeContext context) {
    if (TEMPLATE_NAMING_CONTAINERS.contains(context.containerName()) && context.templateKind() != null) {
      return dataTemplateScopes(context.containerName(), context.templateKind());
    }
    if (WIZARD_NAMING_CONTAINERS.contains(context.containerName())) {
      return wizardTemplateScopes(context);
    }
    return context.exclusiveTemplateKind() == null ? Set.of() : Set.of(context.exclusiveTemplateKind());
  }

  private Set<String> wizardTemplateScopes(NodeContext context) {
    WizardContext wizard = context.wizard();
    TagNode step = wizard.step();
    String navigationTemplate = wizard.navigationTemplate();
    if (navigationTemplate != null) {
      // CustomNavigationTemplate is declared inside a step but rendered in a container of its own,
      // so its scope stays distinct from the content template of the same step.
      return Set.of(step == null ? navigationTemplate : navigationTemplate + "-" + wizardStepScope(step));
    }
    if (step != null) {
      return Set.of(wizardStepScope(step));
    }
    // Header, side bar and layout content renders alongside every step and is not guaranteed to sit
    // in a naming container of its own, so it shares the scope of all the steps.
    return wizardScopes.computeIfAbsent(context.namingContainer(), this::collectWizardStepScopes);
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
    return wizardStepScopes.computeIfAbsent(wizardStep, key -> nextWizardStepScope());
  }

  private String nextWizardStepScope() {
    String scope = "wizard-step-" + nextWizardStepId;
    nextWizardStepId++;
    return scope;
  }

  private WizardContext wizardContext(TagNode node, String localName, WizardContext inherited) {
    TagNode inheritedStep = inherited.step();
    return new WizardContext(
      inheritedStep == null && isWizardStep(node, localName) ? node : inheritedStep,
      nearestMatching(localName, WIZARD_NAVIGATION_TEMPLATE_SCOPES, inherited.navigationTemplate()));
  }

  private boolean isNamingContainer(TagNode node, String localName) {
    return Helpers.isServerControl(node)
      && (isRegisteredUserControl(node)
        || (NAMING_CONTAINERS.contains(localName) && hasKnownPrefix(node)));
  }

  private boolean isRegisteredUserControl(TagNode node) {
    return registeredUserControls.contains(node.getNodeName().toLowerCase(Locale.ROOT));
  }

  private boolean isWizard(TagNode node, String localName) {
    return WIZARD_NAMING_CONTAINERS.contains(localName) && isKnownControl(node);
  }

  private boolean isWizardStep(TagNode node, String localName) {
    // Items declared in a WizardSteps collection are server controls without needing runat="server".
    return WIZARD_STEP_SCOPES.contains(localName) && hasKnownPrefix(node);
  }

  private boolean isKnownControl(TagNode node) {
    return hasKnownPrefix(node) && Helpers.isServerControl(node);
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
    WizardContext wizard) {
  }

  /** The innermost wizard step and navigation template enclosing a node, if any. */
  private record WizardContext(@Nullable TagNode step, @Nullable String navigationTemplate) {
    private static final WizardContext NONE = new WizardContext(null, null);
  }
}
