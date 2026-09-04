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

import org.sonar.check.Rule;
import org.sonar.plugins.html.api.Helpers;
import org.sonar.plugins.html.api.HtmlConstants;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.checks.EmbeddedHtmlCheck;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "S9380")
public class ScopeAttributeOnlyOnThCheck extends AbstractPageCheck implements EmbeddedHtmlCheck {

  private static final String MESSAGE = "Move this \"scope\" attribute to a \"th\" element, or remove it.";

  @Override
  public void startElement(TagNode node) {
    if (!node.hasProperty("scope") || "th".equalsIgnoreCase(node.getNodeName()) || isTemplateTag(node)) {
      return;
    }
    // A custom component or unknown tag: "scope" may be an arbitrary prop, unrelated to table headers.
    if (isComponentReference(node) || !HtmlConstants.hasKnownHTMLTag(node)) {
      return;
    }
    createViolation(node, MESSAGE);
  }

  private static boolean isTemplateTag(TagNode node) {
    // Vue 2.0-2.4 scoped slots use a bare "scope" attribute on <template>, unrelated to table headers.
    return "template".equalsIgnoreCase(node.getNodeName());
  }

  private boolean isComponentReference(TagNode node) {
    String nodeName = node.getNodeName();
    // Kebab-case is always a custom element; PascalCase only means a component in Vue files.
    return isKebabCase(nodeName) || (Helpers.isVueFile(getHtmlSourceCode()) && startsWithUpperCase(nodeName));
  }

  private static boolean isKebabCase(String name) {
    return name.indexOf('-') >= 0;
  }

  private static boolean startsWithUpperCase(String name) {
    return !name.isEmpty() && Character.isUpperCase(name.charAt(0));
  }

}
