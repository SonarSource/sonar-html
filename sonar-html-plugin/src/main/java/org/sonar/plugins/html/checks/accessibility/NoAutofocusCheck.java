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
package org.sonar.plugins.html.checks.accessibility;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.sonar.check.Rule;
import org.sonar.plugins.html.api.FrameworkAttributeBindings;
import org.sonar.plugins.html.api.Helpers;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Attribute;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;

import static org.sonar.plugins.html.api.HtmlConstants.hasKnownHTMLTag;

@Rule(key = "S9379")
public class NoAutofocusCheck extends AbstractPageCheck {

  private static final String MESSAGE = "Remove this \"autofocus\" attribute, as it can reduce usability and accessibility for users.";

  // Spellings of an "autofocus" attribute; excludes Vue's ":[autofocus]" dynamic argument, whose target is only known at runtime.
  private static final Set<String> AUTOFOCUS_ATTRIBUTE_NAMES = FrameworkAttributeBindings.staticAttributeSpellings("autofocus");
  private static final Set<String> AUTOFOCUS_DOM_PROPERTY_BINDINGS = FrameworkAttributeBindings.domPropertyBindingNames("autofocus");

  private boolean isVueFile;

  @Override
  public void startDocument(List<Node> nodes) {
    isVueFile = Helpers.isVueFile(getHtmlSourceCode());
  }

  @Override
  public void startElement(TagNode node) {
    // A false-bound spelling doesn't rule out another, effective spelling of "autofocus" on the same element.
    boolean hasEffectiveAutofocusAttribute = node.getAttributes().stream()
      .filter(a -> AUTOFOCUS_ATTRIBUTE_NAMES.contains(a.getName()))
      .anyMatch(a -> !isDomPropertyBoundToFalse(a));
    if (!hasEffectiveAutofocusAttribute) {
      return;
    }
    // Kebab-case is always a custom element (no native tag has a hyphen). Any other casing only
    // means a component in Vue, since HTML tag names are otherwise case-insensitive, e.g. plain
    // <BUTTON>.
    String nodeName = node.getNodeName();
    boolean componentReference = Helpers.isKebabCase(nodeName)
      || (isVueFile && Helpers.isVueComponentName(nodeName));
    if (componentReference || !hasKnownHTMLTag(node)) {
      return;
    }
    if (!isDialogOrPopover(node) && !Helpers.hasAncestorMatching(node, NoAutofocusCheck::isDialogOrPopover)) {
      createViolation(node, MESSAGE);
    }
  }

  private static boolean isDomPropertyBoundToFalse(Attribute property) {
    String value = property.getValue();
    if (value == null || !"false".equals(value.trim())) {
      return false;
    }
    return AUTOFOCUS_DOM_PROPERTY_BINDINGS.contains(property.getName());
  }

  private static boolean isDialogOrPopover(TagNode node) {
    if ("dialog".equalsIgnoreCase(node.getNodeName()) || node.hasProperty("popover")) {
      return true;
    }
    String role = node.getAttribute("role");
    if (role == null) {
      // static role absent: a bound role (:role/[role]) cannot be resolved, do not report
      return node.getProperty("role") != null;
    }
    return Arrays.stream(role.trim().split("\\s+"))
      .anyMatch(token -> "dialog".equalsIgnoreCase(token) || "alertdialog".equalsIgnoreCase(token));
  }

}
