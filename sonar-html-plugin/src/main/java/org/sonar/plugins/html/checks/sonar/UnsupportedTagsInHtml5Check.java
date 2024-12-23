/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (C) 2010-2024 SonarSource SA
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

  @Override
  public void startElement(TagNode node) {
    if (isUnsupportedTag(node)) {
      createViolation(node, "Remove this deprecated \"" + node.getNodeName() + "\" element.");
    }
  }

  private static boolean isUnsupportedTag(TagNode node) {
    String nodeName = node.getNodeName();
    return UNSUPPORTED_TAGS.contains(nodeName.toUpperCase(Locale.ENGLISH));
  }

}
