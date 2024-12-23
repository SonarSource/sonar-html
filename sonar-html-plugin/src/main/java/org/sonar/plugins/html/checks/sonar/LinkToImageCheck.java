/*
 * SonarQube HTML
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

@Rule(key = "LinkToImageCheck")
public class LinkToImageCheck extends AbstractPageCheck {

  private static final Set<String> IMG_SUFFIXES = Set.of(".GIF", ".JPG", ".JPEG", ".PNG", ".BMP");

  @Override
  public void startElement(TagNode node) {
    if (isATag(node) && hasHrefToImage(node)) {
      createViolation(node, "Change this link to not directly target an image.");
    }
  }

  private static boolean isATag(TagNode node) {
    return "A".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean hasHrefToImage(TagNode node) {
    String href = node.getAttribute("href");

    return href != null &&
      isPointingToAnImage(href);
  }

  private static boolean isPointingToAnImage(String target) {
    final String upperTarget = target.toUpperCase(Locale.ENGLISH);
    return !upperTarget.contains("?") && IMG_SUFFIXES.stream().anyMatch(input -> input != null && upperTarget.endsWith(input));
  }

}
