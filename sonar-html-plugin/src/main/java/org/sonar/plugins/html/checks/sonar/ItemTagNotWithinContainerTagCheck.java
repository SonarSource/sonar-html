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
package org.sonar.plugins.html.checks.sonar;

import java.util.List;
import org.sonar.check.Rule;
import org.sonar.plugins.html.api.Helpers;
import org.sonar.plugins.html.api.RazorSectionScopeTracker;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "ItemTagNotWithinContainerTagCheck")
public class ItemTagNotWithinContainerTagCheck extends AbstractPageCheck {

  private RazorSectionScopeTracker razorSectionScope = RazorSectionScopeTracker.empty();

  @Override
  public void startDocument(List<Node> nodes) {
    razorSectionScope = RazorSectionScopeTracker.create(nodes, getHtmlSourceCode());
  }

  @Override
  public void startElement(TagNode node) {
    if (Helpers.hasTemplateAncestor(node) || razorSectionScope.contains(node)) {
      return;
    }
    if (isLi(node) && !hasLiOrUlOrOlAncestor(node)) {
      createViolation(node, "Surround this <" + node.getNodeName() + "> item tag by a <ul> or <ol> container one.");
    } else if (isDt(node) && !hasDtOrDlAncestor(node)) {
      createViolation(node, "Surround this <" + node.getNodeName() + "> item tag by a <dl> container one.");
    }
  }

  private static boolean hasLiOrUlOrOlAncestor(TagNode node) {
    return Helpers.hasAncestorMatching(node, p -> isLi(p) || isLiAllowedParent(p));
  }

  private static boolean hasDtOrDlAncestor(TagNode node) {
    return Helpers.hasAncestorMatching(node, p -> isDt(p) || isDl(p));
  }

  private static boolean isLi(TagNode node) {
    return "LI".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isDt(TagNode node) {
    return "DT".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isLiAllowedParent(TagNode node) {
    return isUl(node) || isOl(node) || isMenu(node);
  }

  private static boolean isUl(TagNode node) {
    return "UL".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isOl(TagNode node) {
    return "OL".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isMenu(TagNode node) {
    return "MENU".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isDl(TagNode node) {
    return "DL".equalsIgnoreCase(node.getNodeName());
  }

}
