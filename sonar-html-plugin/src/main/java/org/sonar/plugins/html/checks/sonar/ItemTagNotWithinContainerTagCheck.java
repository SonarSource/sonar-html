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

import static org.sonar.plugins.html.api.HtmlConstants.hasKnownHTMLTag;

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
    TagNode parent = effectiveParent(node);
    if (isLi(node) && (parent == null || (hasKnownHTMLTag(parent) && !isLiAllowedParent(parent)))) {
      createViolation(node, "Surround this <" + node.getNodeName() + "> item tag by a <ul> or <ol> container one.");
    } else if (isDt(node) && (parent == null || (hasKnownHTMLTag(parent) && !isDl(parent)))) {
      createViolation(node, "Surround this <" + node.getNodeName() + "> item tag by a <dl> container one.");
    }
  }

  /**
   * Returns the effective direct parent after skipping parser artifacts caused by omitted end tags.
   *
   * @param node the item tag being checked
   * @return the nearest parent that remains structurally relevant for the rule
   */
  private static TagNode effectiveParent(TagNode node) {
    TagNode parent = node.getParent();
    while (parent != null && isImplicitlyClosedBy(node, parent)) {
      parent = parent.getParent();
    }
    return parent;
  }

  /**
   * Returns whether opening the current item tag implicitly closes the parsed parent in HTML.
   *
   * @param node the item tag being checked
   * @param parent the current parsed parent candidate
   * @return true if the parent should be skipped as an omitted-end-tag artifact
   */
  private static boolean isImplicitlyClosedBy(TagNode node, TagNode parent) {
    TagNode grandParent = parent.getParent();
    if (isLi(node)) {
      return isLi(parent) || (isP(parent) && grandParent != null && isLi(grandParent));
    }
    if (isDt(node)) {
      return isDefinitionItem(parent) || (isP(parent) && grandParent != null && isDefinitionItem(grandParent));
    }
    return false;
  }

  private static boolean isDefinitionItem(TagNode node) {
    return isDt(node) || isDd(node);
  }

  private static boolean isLi(TagNode node) {
    return "LI".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isDt(TagNode node) {
    return "DT".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isDd(TagNode node) {
    return "DD".equalsIgnoreCase(node.getNodeName());
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

  private static boolean isP(TagNode node) {
    return "P".equalsIgnoreCase(node.getNodeName());
  }

}
