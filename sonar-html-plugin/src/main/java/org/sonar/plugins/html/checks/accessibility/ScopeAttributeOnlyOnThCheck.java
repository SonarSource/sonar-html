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

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.sonar.check.Rule;
import org.sonar.plugins.html.api.Helpers;
import org.sonar.plugins.html.api.HtmlConstants;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.checks.EmbeddedHtmlCheck;
import org.sonar.plugins.html.node.Attribute;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "S9380")
public class ScopeAttributeOnlyOnThCheck extends AbstractPageCheck implements EmbeddedHtmlCheck {

  private static final String SCOPE = "scope";
  private static final String MESSAGE = "Move this \"scope\" attribute to a \"th\" element, or remove it.";

  // Spellings of a "scope" attribute; excludes Vue's ":[scope]" dynamic argument, whose target is only known at runtime.
  private static final Set<String> SCOPE_ATTRIBUTE_NAMES = Stream.concat(
      Stream.of(SCOPE, "[attr.scope]", "attr.scope"),
      TagNode.domPropertyBindingNames(SCOPE).stream())
    .map(name -> name.toLowerCase(Locale.ROOT))
    .collect(Collectors.toUnmodifiableSet());

  @Override
  public void startElement(TagNode node) {
    // Vue 2.0-2.4 scoped slots use a bare "scope" attribute on <template>, unrelated to table headers.
    if (!hasScopeAttribute(node) || "th".equalsIgnoreCase(node.getNodeName()) || Helpers.isTemplateLikeTag(node)) {
      return;
    }
    // A custom component or unknown tag: "scope" may be an arbitrary prop, unrelated to table headers.
    if (isComponentReference(node) || !HtmlConstants.hasKnownHTMLTag(node)) {
      return;
    }
    createViolation(node, MESSAGE);
  }

  private static boolean hasScopeAttribute(TagNode node) {
    return node.getAttributes().stream().anyMatch(ScopeAttributeOnlyOnThCheck::isEffectiveScopeAttribute);
  }

  private static boolean isEffectiveScopeAttribute(Attribute attribute) {
    return SCOPE_ATTRIBUTE_NAMES.contains(attribute.getName().toLowerCase(Locale.ROOT))
      && !isDomPropertyBoundToNullish(attribute);
  }

  // A DOM-property binding (`:scope`, `v-bind:scope`, `[scope]`) bound to literal null/undefined never sets the attribute.
  private static boolean isDomPropertyBoundToNullish(Attribute property) {
    String value = property.getValue();
    if (value == null || !("null".equals(value.trim()) || "undefined".equals(value.trim()))) {
      return false;
    }
    return TagNode.domPropertyBindingNames(SCOPE).stream()
      .anyMatch(name -> name.equalsIgnoreCase(property.getName()));
  }

  private boolean isComponentReference(TagNode node) {
    String nodeName = node.getNodeName();
    // Kebab-case is always a custom element; PascalCase only means a component in Vue files.
    return Helpers.isKebabCase(nodeName) || (Helpers.isVueFile(getHtmlSourceCode()) && Helpers.startsWithUpperCase(nodeName));
  }

}
