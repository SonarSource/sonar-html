/*
 * SonarQube HTML
 * Copyright (C) 2010-2025 SonarSource SA
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
package org.sonar.plugins.html.checks.coding;

import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;

import java.util.Locale;
import java.util.Set;

@Rule(key = "S1874")
public class DeprecatedCheck extends AbstractPageCheck {

  private static final Set<String> DEPRECATED_TAGS = Set.of(
    "acronym",
    "applet",
    "basefont",
    "big",
    "blink",
    "bgsound",
    "center",
    "dir",
    "font",
    "frame",
    "frameset",
    "isindex",
    "keygen",
    "listing",
    "marquee",
    "multicol",
    "nextid",
    "noframes",
    "plaintext",
    "rb",
    "rtc",
    "spacer",
    "strike",
    "tt",
    "xmp"
  );

  @Override
  public void startElement(TagNode element) {
    String tagName = element.getNodeName().toLowerCase(Locale.ENGLISH);

    if (DEPRECATED_TAGS.contains(tagName)) {
      createViolation(element, String.format("The <%s> tag is deprecated/obsolete and should not be used.", tagName));
    }
  }
}
