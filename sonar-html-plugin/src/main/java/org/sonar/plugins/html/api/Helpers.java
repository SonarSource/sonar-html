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

  private static final Pattern RAZOR_FRAGMENT_RENDERING = Pattern.compile(
    "@(?:RenderBody|RenderSection|RenderPage|(?:await\\s+)?Html\\.(?:Render)?Partial(?:Async)?)\\b");

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
   * Returns true if any ancestor of {@code node} satisfies {@code predicate}.
   *
   * @param node the tag node whose ancestors are inspected
   * @param predicate the test applied to each ancestor
   * @return true if any ancestor satisfies the predicate
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
   * Returns true if {@code node} has a template-like ancestor: HTML {@code <template>},
   * Angular {@code <ng-template>}, or an ASP.NET WebForms server control ({@code asp:*}).
   *
   * @param node the tag node whose ancestors are inspected
   * @return true if any ancestor matches a template-like scope
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
  
  /**
   * Returns true when the source file uses Razor syntax (.cshtml or .vbhtml).
   *
   * @param code the source under analysis
   * @return true if the file is a Razor view, false otherwise
   */
  public static boolean isRazorFile(HtmlSourceCode code) {
    String filename = code.inputFile().filename();
    return filename.endsWith(".cshtml") || filename.endsWith(".vbhtml");
  }

  /**
   * Returns true when the given text contains a Razor expression that renders a body, section, page, or partial.
   *
   * @param text the text fragment to inspect
   * @return true if {@code text} renders a Razor fragment, false otherwise
   */
  public static boolean containsRazorFragmentRendering(String text) {
    return RAZOR_FRAGMENT_RENDERING.matcher(text).find();
  }
}
