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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;
import org.sonar.check.Rule;
import org.sonar.plugins.html.api.Helpers;
import org.sonar.plugins.html.api.TemplateConditionalScopeTracker;
import org.sonar.plugins.html.api.TemplateConditionalScopeTracker.ConditionalAttributeScope;
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
  private List<ConditionalAttributeScope> linkAttributeScopes = List.of();
  @Nullable
  private String linkLabelledBy;
  @Nullable
  private String linkAriaLabel;
  private final TemplateConditionalScopeTracker conditionalScope = new TemplateConditionalScopeTracker();

  // Every non-hidden, rendered <a> is appended here as it closes; comparisons happen once,
  // in endDocument(), once every link in the document is known.
  private final List<LinkRecord> links = new ArrayList<>();

  private final StringBuilder text = new StringBuilder();
  private String target = "";
  private int line;
  private TagNode linkParent;

  @Override
  public void startDocument(List<Node> nodes) {
    links.clear();
    inLink = false;
    conditionalScope.reset(Helpers.isRazorFile(getHtmlSourceCode()));
  }

  @Override
  public void endDocument() {
    Map<TagNode, Map<NameKey, List<LinkRecord>>> groupsByParent = new IdentityHashMap<>();
    for (LinkRecord link : links) {
      groupsByParent.computeIfAbsent(link.parent(), k -> new HashMap<>())
        .computeIfAbsent(link.nameKey(), k -> new ArrayList<>())
        .add(link);
    }
    for (Map<NameKey, List<LinkRecord>> groupsByName : groupsByParent.values()) {
      for (List<LinkRecord> group : groupsByName.values()) {
        compareGroup(group);
      }
    }
  }

  /**
   * Compares links sharing the same parent and accessible name, in document order, against a
   * baseline established by the first link outside any conditional block.
   */
  private void compareGroup(List<LinkRecord> group) {
    Link baseline = null;
    Map<Object, Link> pendingBranchLinks = new HashMap<>();
    List<LinkRecord> pendingAttributeScopedLinks = new ArrayList<>();

    for (LinkRecord link : group) {
      if (!link.conditional()) {
        Link previous = baseline == null ? firstConflictingLink(pendingBranchLinks, link.target()) : baseline;
        reportIfConflicting(link, previous);
        baseline = new Link(link.line(), link.target());
      } else if (baseline != null) {
        reportIfConflicting(link, baseline);
      } else {
        compareConditionalWithoutBaseline(link, pendingBranchLinks, pendingAttributeScopedLinks);
      }
    }
  }

  /**
   * Compares a conditional link against earlier ones seen before any unconditional baseline was
   * established in its group: one in the same text/JSTL branch, or one whose conditional
   * attributes are not mutually exclusive with its own.
   */
  private void compareConditionalWithoutBaseline(
    LinkRecord link,
    Map<Object, Link> pendingBranchLinks,
    List<LinkRecord> pendingAttributeScopedLinks) {
    Link previous = link.branchId() == null ? null : pendingBranchLinks.get(link.branchId());
    if (previous != null) {
      reportIfConflicting(link, previous);
    } else if (link.branchId() == null && !link.attributeScopes().isEmpty()) {
      LinkRecord conflict = firstNonExclusiveConflict(pendingAttributeScopedLinks, link);
      reportIfConflicting(link, conflict == null ? null : new Link(conflict.line(), conflict.target()));
      pendingAttributeScopedLinks.add(link);
    }
    pendingBranchLinks.putIfAbsent(link.branchId(), new Link(link.line(), link.target()));
  }

  private void reportIfConflicting(LinkRecord link, @Nullable Link previous) {
    if (previous != null && !link.target().equals(previous.getTarget())) {
      createViolation(link.line(), "Use a distinct text or label, or point to the same target for this link and the one on line " + previous.getLine() + ".");
    }
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

  /**
   * Returns the earliest previously seen conditional-attribute-scoped link that conflicts with
   * {@code link} and is not guaranteed mutually exclusive with it (e.g. sharing the same
   * {@code *ngIf} host, as opposed to opposite {@code v-if}/{@code v-else} branches).
   */
  @Nullable
  private static LinkRecord firstNonExclusiveConflict(List<LinkRecord> pending, LinkRecord link) {
    LinkRecord earliest = null;
    for (LinkRecord candidate : pending) {
      if (!link.target().equals(candidate.target())
        && canCoexist(link.attributeScopes(), candidate.attributeScopes())
        && (earliest == null || candidate.line() < earliest.line())) {
        earliest = candidate;
      }
    }
    return earliest;
  }

  private static boolean canCoexist(List<ConditionalAttributeScope> firstScopes, List<ConditionalAttributeScope> secondScopes) {
    for (ConditionalAttributeScope firstScope : firstScopes) {
      for (ConditionalAttributeScope secondScope : secondScopes) {
        if (TemplateConditionalScopeTracker.areMutuallyExclusive(firstScope, secondScope)) {
          return false;
        }
      }
    }
    return true;
  }

  @Override
  public void directive(DirectiveNode directiveNode) {
    conditionalScope.visitDirective(directiveNode);
  }

  @Override
  public void startElement(TagNode node) {
    conditionalScope.startElement(node);
    if (isAnchorElement(node)) {
      inLink = true;
      text.delete(0, text.length());
      target = getTarget(node);
      line = node.getStartLinePosition();
      linkParent = node.getParent();
      linkHidden = isHiddenLink(node) || conditionalScope.isInNonRenderedRazorContent();
      linkLabelledBy = nonDynamicPropertyValue(node, "aria-labelledby");
      linkAriaLabel = nonDynamicPropertyValue(node, "aria-label");
      linkAttributeScopes = conditionalScope.conditionalAttributeScopes(node);
      linkInConditional = conditionalScope.isInOpenConditionalScope() || !linkAttributeScopes.isEmpty();
      // A link guarded by its own conditional attribute may be mutually exclusive with a sibling in
      // the same text branch, so it gets no branch identity.
      linkBranchId = linkAttributeScopes.isEmpty() && conditionalScope.isInOpenConditionalScope()
        ? conditionalScope.currentConditionalBranchId()
        : null;
    }
  }

  /**
   * Returns whether {@code node} or any of its ancestors is hidden from every user. Hiding a
   * container also hides everything inside it, so a link can be invisible without carrying any
   * hidden-related attribute itself.
   */
  private static boolean isHiddenLink(TagNode node) {
    for (TagNode current = node; current != null; current = current.getParent()) {
      if (isHidden(current)) {
        return true;
      }
    }
    return false;
  }

  private static boolean isHidden(TagNode node) {
    return AccessibilityUtils.isHiddenFromScreenReader(node) || AccessibilityUtils.hasHiddenAttribute(node);
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
    if (isAnchorElement(node)) {
      inLink = false;
      if (!linkHidden) {
        collectLink();
      }
    }
  }

  private void collectLink() {
    NameKey nameKey = computeNameKey();
    if (nameKey == null) {
      return;
    }
    links.add(new LinkRecord(linkParent, nameKey, target, line, linkInConditional, linkBranchId, linkAttributeScopes));
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

  private static boolean isAnchorElement(TagNode node) {
    return "A".equalsIgnoreCase(node.getNodeName());
  }

  /**
   * A single rendered, non-hidden {@code <a>}, collected during traversal for comparison in {@code endDocument()}.
   *
   * @param conditional whether this link is inside a text/JSTL conditional block or under a conditional-attribute host
   * @param branchId identity of the enclosing text/JSTL branch, or {@code null} when not applicable or not reliably known
   * @param attributeScopes the conditional-attribute hosts (e.g. {@code *ngIf}, {@code v-if}) containing this link
   */
  private record LinkRecord(
    TagNode parent,
    NameKey nameKey,
    String target,
    int line,
    boolean conditional,
    @Nullable Object branchId,
    List<ConditionalAttributeScope> attributeScopes) {
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

  // Which source an accessible-name key came from, so different sources never collide.
  private enum NameSource {
    LABELLEDBY,
    LABEL,
    TEXT
  }

  private record NameKey(NameSource source, String value) {
  }

}
