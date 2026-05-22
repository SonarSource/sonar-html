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
   * Returns true if the given node has an ancestor that suggests this content
   * lives inside an opaque templating scope, where the surrounding markup is
   * supplied by a framework rather than appearing inline in the source file.
   *
   * Recognised scopes:
   * - HTML {@code <template>} element (HTML5 dynamic composition)
   * - Angular {@code <ng-template>}
   * - Any namespaced element (name contains {@code ':'}): {@code asp:Repeater},
   *   {@code c:forEach} (JSTL), {@code th:each} (Thymeleaf), {@code jsp:include}, etc.
   *
   * @param node the tag node whose ancestors are inspected
   * @return true if any ancestor matches a template-like scope, false otherwise
   */
  public static boolean hasTemplateAncestor(TagNode node) {
    TagNode parent = node.getParent();
    while (parent != null) {
      if (isTemplateLikeTag(parent)) {
        return true;
      }
      parent = parent.getParent();
    }
    return false;
  }

  private static boolean isTemplateLikeTag(TagNode node) {
    String name = node.getNodeName();
    if (name == null || name.isEmpty()) {
      return false;
    }
    return "template".equalsIgnoreCase(name)
      || "ng-template".equalsIgnoreCase(name)
      || name.indexOf(':') >= 0;
  }
}
