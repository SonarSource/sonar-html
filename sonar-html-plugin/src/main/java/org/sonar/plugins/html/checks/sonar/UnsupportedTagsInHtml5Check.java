/*
 * SonarQube HTML
 * Copyright (C) 2010-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
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

import java.util.Locale;
import java.util.Set;
import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;

/** RSPEC-1083 */
@Rule(key = "UnsupportedTagsInHtml5Check")
public class UnsupportedTagsInHtml5Check extends AbstractPageCheck {

  private static final Set<String> UNSUPPORTED_TAGS = Set.of(
      "ACRONYM",
      "APPLET",
      "BASEFONT",
      "BGSOUND",
      "BIG",
      "BLINK",
      "CENTER",
      "DIR",
      "FONT",
      "FRAME",
      "FRAMESET",
      "ISINDEX",
      "LISTING",
      "MARQUEE",
      "MULTICOL",
      "NEXTID",
      "NOBR",
      "NOEMBED",
      "NOFRAMES",
      "PLAINTEXT",
      "SPACER",
      "STRIKE",
      "TT",
      "XMP");

  private boolean isVueFile;

  @Override
  public void startDocument(java.util.List<org.sonar.plugins.html.node.Node> nodes) {
    String filename = getHtmlSourceCode().inputFile().filename();
    isVueFile = filename.endsWith(".vue");
  }

  @Override
  public void startElement(TagNode node) {
    if (isUnsupportedTag(node)) {
      createViolation(node, "Remove this deprecated \"" + node.getNodeName() + "\" element.");
    }
  }

  private boolean isUnsupportedTag(TagNode node) {
    String nodeName = node.getNodeName();

    // In Vue files, PascalCase tags are components, not HTML elements
    // e.g., <BLink> is a Vue Bootstrap component, not the deprecated <blink> tag
    if (isVueFile && isPascalCase(nodeName)) {
      return false;
    }

    return UNSUPPORTED_TAGS.contains(nodeName.toUpperCase(Locale.ENGLISH));
  }

  /**
   * Checks if a tag name uses PascalCase (has uppercase letter after the first character).
   * In Vue templates, PascalCase indicates a component reference.
   */
  private static boolean isPascalCase(String name) {
    for (int i = 1; i < name.length(); i++) {
      if (Character.isUpperCase(name.charAt(i))) {
        return true;
      }
    }
    return false;
  }

}
