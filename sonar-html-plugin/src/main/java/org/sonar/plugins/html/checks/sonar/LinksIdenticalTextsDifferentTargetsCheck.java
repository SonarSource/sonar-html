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

import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;
import org.sonar.check.Rule;
import org.sonar.plugins.html.api.Helpers;
import org.sonar.plugins.html.api.TemplateConditionalScopeTracker;
import org.sonar.plugins.html.api.accessibility.AccessibilityUtils;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.DirectiveNode;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;
import org.sonar.plugins.html.node.TextNode;

@Rule(key = "LinksIdenticalTextsDifferentTargetsCheck")
public class LinksIdenticalTextsDifferentTargetsCheck extends AbstractPageCheck {

  private boolean inLink;
  private boolean linkHidden;
  private boolean linkInConditional;
  @Nullable
  private Object linkBranchId;
  @Nullable
  private String linkLabelledBy;
  @Nullable
  private String linkAriaLabel;
  private final TemplateConditionalScopeTracker conditionalScope = new TemplateConditionalScopeTracker();
  // Outer key: parent TagNode (null = document root). Inner key: the link's accessible name, see NameKey.
  // Only holds links seen outside any conditional branch: the only occurrences guaranteed to
  // render, and therefore the only reliable baseline to compare other links against.
  private final Map<TagNode, Map<NameKey, Link>> unconditionalLinksByParent = new IdentityHashMap<>();
  // Same keying as above, plus branch identity (null when a branch can't be reliably told apart from a sibling).
  private final Map<TagNode, Map<NameKey, Map<Object, Link>>> pendingConditionalLinksByParent = new IdentityHashMap<>();

  private final StringBuilder text = new StringBuilder();
  private String target = "";
  private int line;
  private TagNode linkParent;

  @Override
  public void startDocument(List<Node> nodes) {
    unconditionalLinksByParent.clear();
    pendingConditionalLinksByParent.clear();
    inLink = false;
    conditionalScope.reset(Helpers.isRazorFile(getHtmlSourceCode()));
  }

  @Override
  public void directive(DirectiveNode directiveNode) {
    conditionalScope.visitDirective(directiveNode);
  }

  @Override
  public void startElement(TagNode node) {
    conditionalScope.startElement(node);
    if (isA(node)) {
      inLink = true;
      text.delete(0, text.length());
      target = getTarget(node);
      line = node.getStartLinePosition();
      linkParent = node.getParent();
      linkHidden = isHiddenLink(node) || conditionalScope.isInNonRenderedRazorContent();
      linkLabelledBy = nonDynamicPropertyValue(node, "aria-labelledby");
      linkAriaLabel = nonDynamicPropertyValue(node, "aria-label");
      List<TemplateConditionalScopeTracker.ConditionalAttributeScope> attributeScopes = conditionalScope.conditionalAttributeScopes(node);
      linkInConditional = conditionalScope.isInOpenConditionalScope() || !attributeScopes.isEmpty();
      // A link guarded by its own conditional attribute may be mutually exclusive with a sibling in
      // the same text branch, so it gets no branch identity.
      linkBranchId = attributeScopes.isEmpty() && conditionalScope.isInOpenConditionalScope()
        ? conditionalScope.currentConditionalBranchId()
        : null;
    }
  }

  private static boolean isHiddenLink(TagNode node) {
    return AccessibilityUtils.isHiddenFromScreenReader(node) || AccessibilityUtils.isHiddenByDisplayNone(node);
  }

  private static String getTarget(TagNode node) {
    String t = node.getPropertyValue("href");
    return t == null ? "" : t;
  }

  @Override
  public void characters(TextNode textNode) {
    conditionalScope.visitText(textNode);
    if (inLink) {
      text.append(textNode.getCode());
    }
  }

  @Override
  public void endElement(TagNode node) {
    conditionalScope.endElement(node);
    if (isA(node)) {
      inLink = false;
      if (!linkHidden) {
        recordOrCompareLink();
      }
    }
  }

  private void recordOrCompareLink() {
    NameKey nameKey = computeNameKey();
    if (nameKey == null) {
      return;
    }

    Map<NameKey, Link> siblingLinks = unconditionalLinksByParent.computeIfAbsent(linkParent, k -> new HashMap<>());
    Link previousLink = siblingLinks.get(nameKey);
    if (previousLink == null) {
      Map<Object, Link> pendingBranches = pendingBranches(nameKey);
      if (linkInConditional) {
        previousLink = linkBranchId == null ? null : pendingBranches.get(linkBranchId);
      } else {
        previousLink = firstConflictingLink(pendingBranches, target);
      }
    }

    if (previousLink != null && !target.equals(previousLink.getTarget())) {
      createViolation(line, "Use a distinct text or label, or point to the same target for this link and the one on line " + previousLink.getLine() + ".");
    }

    if (linkInConditional) {
      pendingConditionalLinksByParent.computeIfAbsent(linkParent, k -> new HashMap<>())
        .computeIfAbsent(nameKey, k -> new HashMap<>())
        .putIfAbsent(linkBranchId, new Link(line, target));
    } else {
      siblingLinks.put(nameKey, new Link(line, target));
    }
  }

  /**
   * Accessible-name comparison key, by precedence: {@code aria-labelledby} (id-ref), then
   * {@code aria-label}, then text content.
   */
  @Nullable
  private NameKey computeNameKey() {
    if (linkLabelledBy != null) {
      String normalized = normalizeWhitespace(linkLabelledBy);
      if (!normalized.isEmpty()) {
        return new NameKey(NameSource.LABELLEDBY, normalized);
      }
    }
    if (linkAriaLabel != null) {
      String normalized = normalizeWhitespace(linkAriaLabel).toUpperCase(Locale.ENGLISH);
      if (!normalized.isEmpty()) {
        return new NameKey(NameSource.LABEL, normalized);
      }
    }
    String upperText = text.toString().toUpperCase(Locale.ENGLISH).trim();
    return upperText.isEmpty() ? null : new NameKey(NameSource.TEXT, upperText);
  }

  private static String normalizeWhitespace(String value) {
    return value.trim().replaceAll("\\s+", " ");
  }

  /**
   * Returns the value of {@code propertyName} on {@code node}, or {@code null} when the attribute
   * is absent, blank, or a template/server-side expression whose runtime value can't be known statically.
   */
  @Nullable
  private String nonDynamicPropertyValue(TagNode node, String propertyName) {
    String value = node.getPropertyValue(propertyName);
    if (value == null || value.isBlank() || Helpers.isDynamicValue(value, getHtmlSourceCode())) {
      return null;
    }
    return value;
  }

  private Map<Object, Link> pendingBranches(NameKey nameKey) {
    Map<NameKey, Map<Object, Link>> byName = pendingConditionalLinksByParent.get(linkParent);
    if (byName == null) {
      return Collections.emptyMap();
    }
    return byName.getOrDefault(nameKey, Collections.emptyMap());
  }

  @Nullable
  private static Link firstConflictingLink(Map<Object, Link> pendingBranches, String target) {
    Link earliest = null;
    for (Link candidate : pendingBranches.values()) {
      if (!target.equals(candidate.getTarget()) && (earliest == null || candidate.getLine() < earliest.getLine())) {
        earliest = candidate;
      }
    }
    return earliest;
  }

  private static boolean isA(TagNode node) {
    return "A".equalsIgnoreCase(node.getNodeName());
  }

  private static class Link {

    private final int line;
    private final String target;

    public Link(int line, String target) {
      this.line = line;
      this.target = target;
    }

    public int getLine() {
      return line;
    }

    public String getTarget() {
      return target;
    }

  }

  /**
   * The ARIA source a link's accessible-name comparison key was derived from. Kept distinct from
   * the key's value so links compared via different sources (e.g. an id reference vs. literal text)
   * never collide even if their normalized values happen to match.
   */
  private enum NameSource {
    LABELLEDBY,
    LABEL,
    TEXT
  }

  private record NameKey(NameSource source, String value) {
  }

}
