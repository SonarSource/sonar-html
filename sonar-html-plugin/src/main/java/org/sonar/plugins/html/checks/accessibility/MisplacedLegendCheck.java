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

import static org.sonar.plugins.html.api.HtmlConstants.hasKnownHTMLTag;

import java.util.List;
import org.sonar.check.Rule;
import org.sonar.plugins.html.api.Helpers;
import org.sonar.plugins.html.api.RazorSectionScopeTracker;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "S8732")
public class MisplacedLegendCheck extends AbstractPageCheck {

  private static final String MESSAGE = "Move this <legend> element to be a direct child of a <fieldset> or <optgroup> element.";
  private RazorSectionScopeTracker razorSectionScope = RazorSectionScopeTracker.empty();

  @Override
  public void startDocument(List<Node> nodes) {
    razorSectionScope = RazorSectionScopeTracker.create(nodes, getHtmlSourceCode());
  }

  @Override
  public void startElement(TagNode node) {
    // Filter legend elements
    if (!isLegend(node)) {
      return;
    }
    TagNode parent = node.getParent();
    // Orphan legend: no parent at all, unless the surrounding <fieldset> is provided by a Razor section host.
    if (parent == null) {
      if (razorSectionScope.contains(node)) {
        return;
      }
      createViolation(node, MESSAGE);
      return;
    }
    // Template-like direct parent (<template>, <ng-template>, asp:*): rendered structure is
    // unknowable (Vue/Angular slots, ASP.NET server-rendered HTML), skip
    if (Helpers.isTemplateLikeTag(parent)) {
      return;
    }
    // Allowed parent: compliant
    String parentName = parent.getNodeName();
    if ("FIELDSET".equalsIgnoreCase(parentName) || "OPTGROUP".equalsIgnoreCase(parentName)) {
      return;
    }
    // Framework/custom element: skip to avoid false positives on framework-rendered markup
    if (!hasKnownHTMLTag(parent)) {
      return;
    }
    createViolation(node, MESSAGE);
  }

  private static boolean isLegend(TagNode node) {
    return "LEGEND".equalsIgnoreCase(node.getNodeName());
  }

}
