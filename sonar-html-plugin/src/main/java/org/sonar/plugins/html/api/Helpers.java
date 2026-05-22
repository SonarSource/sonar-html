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
package org.sonar.plugins.html.api;

import org.sonar.plugins.html.node.TagNode;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public class Helpers {
  private Helpers() {
  }

  public static boolean isHeadingTag(TagNode node) {
    return node.getNodeName().length() == 2 &&
      Character.toUpperCase(node.getNodeName().charAt(0)) == 'H' &&
      node.getNodeName().charAt(1) >= '1' &&
      node.getNodeName().charAt(1) <= '6';
  }

  public static boolean isDynamicValue(String value, HtmlSourceCode code) {
    return value.startsWith("<?php") || value.startsWith("{{") || value.startsWith("{%") || value.startsWith("<?=") ||
            value.startsWith("${") || value.startsWith("<%") ||
            (isCshtmlFile(code) && Pattern.compile("(?<!@)@(?!@)").matcher(value).find());
  }

  public static boolean isCshtmlFile(HtmlSourceCode code) {
    return code.inputFile().filename().endsWith(".cshtml");
  }

  /**
   * Walks the ancestor chain of {@code node} and returns true as soon as any
   * ancestor matches {@code predicate}.
   *
   * @param node the tag node whose ancestors are inspected
   * @param predicate the test applied to each ancestor
   * @return true if any ancestor satisfies the predicate, false otherwise
   */
  public static boolean hasAncestorMatching(TagNode node, Predicate<TagNode> predicate) {
    TagNode parent = node.getParent();
    while (parent != null) {
      if (predicate.test(parent)) {
        return true;
      }
      parent = parent.getParent();
    }
    return false;
  }

  /**
   * Returns true if the given node has an ancestor that supplies the
   * surrounding container itself (rather than being part of the inline
   * source). Only narrow, container-providing scopes are recognised:
   *
   * - HTML {@code <template>} element (HTML5 dynamic composition)
   * - Angular {@code <ng-template>}
   * - ASP.NET WebForms server controls ({@code asp:Repeater},
   *   {@code asp:DataList}, etc.) — their HeaderTemplate/FooterTemplate
   *   pair provably wraps the ItemTemplate content.
   *
   * Pure control-flow or no-op wrapper tags ({@code c:if}, {@code th:block},
   * {@code jsp:include}, …) are NOT treated as template scopes — they don't
   * supply a container of their own.
   *
   * @param node the tag node whose ancestors are inspected
   * @return true if any ancestor matches a template-like scope, false otherwise
   */
  public static boolean hasTemplateAncestor(TagNode node) {
    return hasAncestorMatching(node, Helpers::isTemplateLikeTag);
  }

  private static boolean isTemplateLikeTag(TagNode node) {
    String name = node.getNodeName();
    if (name == null || name.isEmpty()) {
      return false;
    }
    return "template".equalsIgnoreCase(name)
      || "ng-template".equalsIgnoreCase(name)
      || startsWithIgnoreCase(name, "asp:");
  }

  private static boolean startsWithIgnoreCase(String value, String prefix) {
    return value.length() >= prefix.length()
      && value.regionMatches(true, 0, prefix, 0, prefix.length());
  }
}
