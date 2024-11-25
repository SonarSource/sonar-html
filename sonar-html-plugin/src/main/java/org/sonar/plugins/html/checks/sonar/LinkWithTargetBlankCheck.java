/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (C) 2010-2024 SonarSource SA and Matthijs Galesloot
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

import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Pattern;
import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "S5148")
public class LinkWithTargetBlankCheck extends AbstractPageCheck {

  private static final Pattern DYNAMIC_URL = Pattern.compile("[{}$()\\[\\]]");

  @Override
  public void startElement(TagNode node) {
    if (isAnchor(node) && isInsecureUrl(node) && isVulnerable(node)) {
      createViolation(node, "Make sure using target=\"_blank\" and rel=\"opener\" is safe here.");
    }
  }

  private static boolean isVulnerable(TagNode node) {
    return hasBlankTarget(node) && hasRelOpener(node);
  }

  private static boolean hasBlankTarget(TagNode node) {
    String target = node.getPropertyValue("TARGET");
    return "_blank".equalsIgnoreCase(target);
  }

  private static boolean hasRelOpener(TagNode node) {
    String rel = node.getPropertyValue("REL");
    if (rel == null) {
      return false;
    }
    return Arrays.stream(rel.split(" "))
      .map(s -> s.trim().toUpperCase(Locale.ROOT))
      .anyMatch("opener"::equalsIgnoreCase);
  }

  private static boolean isAnchor(TagNode node) {
    return node.equalsElementName("A");
  }

  private static boolean isInsecureUrl(TagNode node) {
    String href = node.getPropertyValue("HREF");
    if (href == null) {
      return false;
    }
    boolean external = isExternalUrl(href);
    boolean dynamic = isDynamic(href);
    return (external && !dynamic) || (!external && dynamic && !isRelativelUrl(href));
  }

  private static boolean isDynamic(String href) {
    return DYNAMIC_URL.matcher(href).find();
  }

  private static boolean isExternalUrl(String href) {
    return href.startsWith("http://") || href.startsWith("https://");
  }

  private static boolean isRelativelUrl(String href) {
    return href.startsWith("/") || href.startsWith(".");
  }
}
