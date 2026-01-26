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
package org.sonar.plugins.html.checks.security;

import java.util.Set;
import java.util.Locale;
import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Attribute;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "S5725")
public class ResourceIntegrityCheck extends AbstractPageCheck {

  private static final String MESSAGE = "Make sure not using resource integrity feature is safe here.";
  private static final Set<String> LINK_REL_VALUES = Set.of("stylesheet", "preload", "modulepreload");

  @Override
  public void startElement(TagNode node) {
    if ((node.equalsElementName("script") && hasExternalSource(node, "src")) ||
      (node.equalsElementName("link") && hasIntegrityRelevantRel(node) && hasExternalSource(node, "href"))) {
      createViolation(node, MESSAGE);
    }
  }

  private static boolean hasExternalSource(TagNode node, String sourceAttribute) {
    Attribute source = node.getProperty(sourceAttribute);
    return source != null && isExternal(source.getValue()) && !node.hasProperty("integrity");
  }

  private static boolean hasIntegrityRelevantRel(TagNode node) {
    Attribute rel = node.getProperty("rel");
    return rel != null && LINK_REL_VALUES.contains(rel.getValue().toLowerCase(Locale.ROOT));
  }

  private static boolean isExternal(String srcValue) {
    return srcValue.startsWith("http") || srcValue.startsWith("//");
  }

}
