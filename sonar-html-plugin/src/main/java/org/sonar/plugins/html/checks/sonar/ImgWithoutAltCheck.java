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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import org.sonar.check.Rule;
import org.sonar.plugins.html.api.Thymeleaf;
import org.sonar.plugins.html.api.accessibility.AccessibilityUtils;
import org.sonar.plugins.html.api.accessibility.AccessibleNameExemption;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.ExpressionNode;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;
import org.sonar.plugins.html.node.TextNode;

@Rule(key = "ImgWithoutAltCheck")
public class ImgWithoutAltCheck extends AbstractPageCheck {
  private static final String MISSING_ALT_MESSAGE_TEMPLATE = "This %s lacks an accessible name; add an \"alt\" attribute (or \"aria-label\"/\"aria-labelledby\").";
  private static final String DECORATIVE_HINT = " Alternatively, mark it as decorative (e.g. aria-hidden or role=\"presentation\").";
  private static final String IMG_MESSAGE = String.format(MISSING_ALT_MESSAGE_TEMPLATE, "<img>") + DECORATIVE_HINT;
  private static final String AREA_MESSAGE = String.format(MISSING_ALT_MESSAGE_TEMPLATE, "<area>");
  private static final String INPUT_IMAGE_MESSAGE = String.format(MISSING_ALT_MESSAGE_TEMPLATE, "<input type=\"image\">");
  private static final String SVG_MESSAGE = "This <svg> lacks an accessible name; add a \"title\" child, \"aria-label\", or \"aria-labelledby\"." + DECORATIVE_HINT;

  /**
   * Tracks one currently-open {@code <svg>}: whether it is already known to need no accessible
   * name (hidden from AT, or decorative), and whether a direct-child {@code <title>} with
   * non-blank text has been found while streaming through its children.
   */
  private static final class SvgTracker {
    private final TagNode svgNode;
    private final boolean exempt;
    private boolean trackingTitle;
    private boolean titleTextFound;

    private SvgTracker(TagNode svgNode, boolean exempt) {
      this.svgNode = svgNode;
      this.exempt = exempt;
    }
  }

  private final Deque<SvgTracker> openSvgs = new ArrayDeque<>();

  @Override
  public void startDocument(List<Node> nodes) {
    openSvgs.clear();
  }

  @Override
  public void startElement(TagNode node) {
    if (isImgTag(node) && !hasImgAlternativeText(node)) {
      createViolation(node, IMG_MESSAGE);
      return;
    }
    if (isImageInput(node) && !hasRequiredAlternativeText(node)) {
      createViolation(node, INPUT_IMAGE_MESSAGE);
      return;
    }
    if (isAreaTag(node) && !hasRequiredAlternativeText(node)) {
      createViolation(node, AREA_MESSAGE);
      return;
    }
    if (isSvgTag(node)) {
      boolean exempt = hasAccessibleName(node)
        || AccessibleNameExemption.isHiddenFromAssistiveTech(node)
        || AccessibleNameExemption.hasEffectivelyPresentationalRole(node);
      openSvgs.push(new SvgTracker(node, exempt));
      return;
    }
    SvgTracker current = openSvgs.peek();
    if (current != null && !current.exempt && isTitleTag(node) && node.getParent() == current.svgNode) {
      current.trackingTitle = true;
      if (AccessibilityUtils.hasNonEmptyTemplateTextAttribute(node)) {
        // th:text/th:utext/v-text/v-html or an [innerHTML]/[innerText]/[textContent] binding
        // supplies the title's text at render time; we can't see the value, so assume it's there.
        current.titleTextFound = true;
      }
    }
  }

  @Override
  public void characters(TextNode textNode) {
    SvgTracker current = openSvgs.peek();
    if (current != null && current.trackingTitle && !textNode.isBlank()) {
      current.titleTextFound = true;
    }
  }

  @Override
  public void expression(ExpressionNode node) {
    // JSP-style <%= ... %> expression inside the tracked <title>: value unknown, assume present.
    SvgTracker current = openSvgs.peek();
    if (current != null && current.trackingTitle) {
      current.titleTextFound = true;
    }
  }

  @Override
  public void endElement(TagNode node) {
    SvgTracker current = openSvgs.peek();
    if (current == null) {
      return;
    }
    if (current.trackingTitle && isTitleTag(node)) {
      current.trackingTitle = false;
    }
    if (isSvgTag(node)) {
      openSvgs.pop();
      if (!current.exempt && !current.titleTextFound) {
        createViolation(current.svgNode, SVG_MESSAGE);
      }
    }
  }

  private static boolean isSvgTag(TagNode node) {
    return "SVG".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isTitleTag(TagNode node) {
    return "TITLE".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isImgTag(TagNode node) {
    return "IMG".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isImageInput(TagNode node) {
    String type = node.getPropertyValue("TYPE");
    return "INPUT".equalsIgnoreCase(node.getNodeName()) && "IMAGE".equalsIgnoreCase(type);
  }

  private static boolean isAreaTag(TagNode node) {
    return "AREA".equalsIgnoreCase(node.getNodeName());
  }

  /**
   * Returns whether an image element exposes alternative text, or is exempt from needing one
   * because it is hidden from assistive technology or marked decorative (e.g. {@code role="presentation"}).
   *
   * @param node the image element to inspect
   * @return {@code true} when the image has an alt-like alternative text, or does not need one
   */
  private static boolean hasImgAlternativeText(TagNode node) {
    return node.hasProperty("alt")
      || hasAccessibleName(node)
      || hasThymeleafAltAttribute(node)
      || AccessibleNameExemption.isHiddenFromAssistiveTech(node)
      || AccessibleNameExemption.hasEffectivelyPresentationalRole(node);
  }

  /**
   * Returns whether an area or image button exposes a non-empty alternative text.
   *
   * @param node the area or input element to inspect
   * @return {@code true} when a non-empty alt-like alternative text is present
   */
  private static boolean hasRequiredAlternativeText(TagNode node) {
    return hasNonEmptyAttribute(node, "alt") || hasAccessibleName(node);
  }

  /**
   * Returns whether the element has a non-empty accessible name.
   *
   * @param node the element to inspect
   * @return {@code true} when aria-label or aria-labelledby is set
   */
  private static boolean hasAccessibleName(TagNode node) {
    return hasNonEmptyAttribute(node, "aria-label") || hasNonEmptyAttribute(node, "aria-labelledby");
  }

  /**
   * Returns whether an attribute is present with a non-empty value, including Thymeleaf variants.
   *
   * @param node the element to inspect
   * @param attributeName the attribute name to resolve
   * @return {@code true} when the attribute exists with a non-empty value
   */
  private static boolean hasNonEmptyAttribute(TagNode node, String attributeName) {
    String value = node.getPropertyValue(attributeName);
    if (value != null && !value.trim().isEmpty()) {
      return true;
    }
    return Thymeleaf.hasNonEmptyThymeleafAttribute(node, attributeName);
  }

  /**
   * In Thymeleaf there are multiple ways of specifying the alt for an img tag:
   * - using the th:alt or th:alt-title attributes (th:alt-title would set the title and alt to the same value)
   * - using the th:attr attribute for specifying different attributes. Example "th:attr="src=@{logo.png},title=#{logo},alt=#{logo}""
   */
  private static boolean hasThymeleafAltAttribute(TagNode node) {
    return node.hasProperty("th:alt") || node.hasProperty("th:alt-title") || Thymeleaf.hasAttrAssignment(node, "alt");
  }
}
