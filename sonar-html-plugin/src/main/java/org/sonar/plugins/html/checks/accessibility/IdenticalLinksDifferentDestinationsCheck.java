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

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;
import org.sonar.check.Rule;
import org.sonar.plugins.html.api.Helpers;
import org.sonar.plugins.html.api.HtmlConstants;
import org.sonar.plugins.html.api.accessibility.AccessibilityUtils;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.DirectiveNode;
import org.sonar.plugins.html.node.ExpressionNode;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;
import org.sonar.plugins.html.node.TextNode;

@Rule(key = "S9384")
public class IdenticalLinksDifferentDestinationsCheck extends AbstractPageCheck {

  private static final String MESSAGE = "This link has the same text as the one on line %d, but points to a different destination.";
  private static final String HTTPS_SCHEME = "https://";

  private final Map<String, LinkRecord> linksByName = new HashMap<>();

  private boolean inLink;
  private TagNode currentLinkNode;
  private String hrefRaw;
  private boolean hrefDynamic;
  private boolean ariaLabelledbyPresent;
  private String ariaLabelRaw;
  private String titleRaw;
  private final StringBuilder content = new StringBuilder();
  private boolean nameDynamic;
  private int hiddenDepth;

  @Override
  public void startDocument(List<Node> nodes) {
    linksByName.clear();
    inLink = false;
    hiddenDepth = 0;
  }

  @Override
  public void endDocument() {
    linksByName.clear();
  }

  @Override
  public void startElement(TagNode node) {
    if (isLinkWithHref(node)) {
      startLink(node);
    } else if (inLink) {
      boolean hidden = AccessibilityUtils.isHiddenFromScreenReader(node);
      if (isVoidElement(node)) {
        // Void elements never affect hiddenDepth: no children, and endElement may never fire.
        if (hiddenDepth == 0 && !hidden) {
          contributeDescendant(node);
        }
      } else if (hiddenDepth > 0) {
        // Already hidden: every open/close pair balances back to 0 at the hiding ancestor's close.
        hiddenDepth++;
      } else if (hidden) {
        hiddenDepth = 1;
      } else {
        contributeDescendant(node);
      }
    }
  }

  @Override
  public void endElement(TagNode node) {
    if (inLink && isA(node)) {
      finalizeLink();
      inLink = false;
    } else if (inLink && !isVoidElement(node) && hiddenDepth > 0) {
      hiddenDepth--;
    }
  }

  @Override
  public void characters(TextNode textNode) {
    if (inLink && hiddenDepth == 0) {
      appendContent(textNode.getCode());
    }
  }

  @Override
  public void expression(ExpressionNode node) {
    if (inLink && hiddenDepth == 0) {
      nameDynamic = true;
    }
  }

  @Override
  public void directive(DirectiveNode node) {
    if (inLink && hiddenDepth == 0) {
      nameDynamic = true;
    }
  }

  private void startLink(TagNode node) {
    inLink = true;
    currentLinkNode = node;
    hrefRaw = node.getAttribute("href");
    hrefDynamic = isDynamic(hrefRaw);
    ariaLabelledbyPresent = isNonBlank(node.getAttribute("aria-labelledby"));
    ariaLabelRaw = node.getAttribute("aria-label");
    titleRaw = node.getAttribute("title");
    content.setLength(0);
    nameDynamic = false;
    hiddenDepth = 0;
  }

  private void contributeDescendant(TagNode node) {
    if (isImg(node)) {
      String alt = node.getAttribute("alt");
      if (isNonBlank(alt)) {
        appendContent(alt);
      }
    }
    String templateText = AccessibilityUtils.getTemplateTextValue(node);
    if (templateText != null) {
      appendContent(templateText);
    }
  }

  private void appendContent(String text) {
    content.append(text);
    if (isDynamic(text)) {
      nameDynamic = true;
    }
  }

  private void finalizeLink() {
    if (!hrefDynamic && isNonBlank(hrefRaw)) {
      String name = resolveAccessibleName();
      if (name != null) {
        String normalizedName = normalize(name);
        if (!normalizedName.isEmpty()) {
          compareAndReport(normalizedName, normalizeDestination(hrefRaw.trim()));
        }
      }
    }
    currentLinkNode = null;
  }

  private String resolveAccessibleName() {
    if (ariaLabelledbyPresent) {
      return null;
    }
    if (isNonBlank(ariaLabelRaw)) {
      return isDynamic(ariaLabelRaw) ? null : ariaLabelRaw;
    }
    if (nameDynamic) {
      return null;
    }
    String contentText = content.toString();
    if (!contentText.trim().isEmpty()) {
      return contentText;
    }
    if (isNonBlank(titleRaw)) {
      return isDynamic(titleRaw) ? null : titleRaw;
    }
    return null;
  }

  private void compareAndReport(String normalizedName, String destination) {
    LinkRecord existing = linksByName.get(normalizedName);
    if (existing == null) {
      linksByName.put(normalizedName, new LinkRecord(currentLinkNode.getStartLinePosition(), destination));
    } else if (!existing.destination.equals(destination)) {
      createViolation(currentLinkNode, String.format(MESSAGE, existing.line));
      linksByName.put(normalizedName, new LinkRecord(currentLinkNode.getStartLinePosition(), destination));
    }
  }

  private boolean isDynamic(@Nullable String value) {
    return value != null && Helpers.containsDynamicValue(value, getHtmlSourceCode());
  }

  private static boolean isLinkWithHref(TagNode node) {
    return isA(node) && node.hasAttribute("href");
  }

  private static boolean isA(TagNode node) {
    return "a".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isImg(TagNode node) {
    return "img".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isVoidElement(TagNode node) {
    return HtmlConstants.VOID_ELEMENTS.contains(node.getNodeName().toLowerCase(Locale.ROOT));
  }

  private static boolean isNonBlank(@Nullable String value) {
    return value != null && !value.trim().isEmpty();
  }

  private static String normalize(String text) {
    return text.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
  }

  private static String normalizeDestination(String href) {
    int hashIndex = href.indexOf('#');
    String base = hashIndex >= 0 ? href.substring(0, hashIndex) : href;
    String fragment = hashIndex >= 0 ? href.substring(hashIndex) : "";
    String normalizedBase = normalizeScheme(base);
    return isRoutingFragment(fragment) ? (normalizedBase + fragment) : normalizedBase;
  }

  private static boolean isRoutingFragment(String fragment) {
    if (fragment.length() < 2) {
      return false;
    }
    String rest = fragment.substring(1);
    return rest.startsWith("/") || rest.startsWith("!/");
  }

  private static String normalizeScheme(String value) {
    if (value.regionMatches(true, 0, "http://", 0, 7)) {
      return HTTPS_SCHEME + value.substring(7);
    }
    if (value.regionMatches(true, 0, HTTPS_SCHEME, 0, HTTPS_SCHEME.length())) {
      return HTTPS_SCHEME + value.substring(HTTPS_SCHEME.length());
    }
    return value;
  }

  private static final class LinkRecord {
    private final int line;
    private final String destination;

    private LinkRecord(int line, String destination) {
      this.line = line;
      this.destination = destination;
    }
  }
}
