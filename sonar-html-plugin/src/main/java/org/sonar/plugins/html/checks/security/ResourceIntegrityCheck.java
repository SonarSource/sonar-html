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
package org.sonar.plugins.html.checks.security;

import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Attribute;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "S5725")
public class ResourceIntegrityCheck extends AbstractPageCheck {

  private static final String MSG_MISSING_BOTH = "Add integrity and crossorigin=\"anonymous\" attributes to this element to enforce integrity checks.";
  private static final String MSG_MISSING_INTEGRITY = "Add an integrity attribute to this element to enforce integrity checks.";
  private static final String MSG_MISSING_CROSSORIGIN = "Add a crossorigin=\"anonymous\" attribute to this element to enforce integrity checks.";

  private static final Set<String> LINK_REL_VALUES = Set.of("stylesheet", "preload", "modulepreload");

  // Matches a semver path segment (/3.7.1/, /v5.3.0/) or a package@version alias (/jquery@3.7.1/)
  private static final Pattern VERSION_PATTERN = Pattern.compile("/((v?\\d+\\.\\d+(\\.\\d+)?)|([^/@]*@[\\d.]+))/");

  @Override
  public void startElement(TagNode node) {
    if (node.equalsElementName("script")) {
      checkElement(node, "src");
    } else if (node.equalsElementName("link") && hasIntegrityRelevantRel(node)) {
      checkElement(node, "href");
    }
  }

  private void checkElement(TagNode node, String sourceAttribute) {
    Attribute source = node.getProperty(sourceAttribute);
    if (source == null || !isExternal(source.getValue()) || !hasVersionInUrl(source.getValue())) {
      return;
    }

    boolean hasIntegrity = node.hasProperty("integrity");
    boolean hasCrossorigin = hasCrossoriginAnonymous(node);

    if (!hasIntegrity && !hasCrossorigin) {
      createViolation(node, MSG_MISSING_BOTH);
    } else if (!hasIntegrity) {
      createViolation(node, MSG_MISSING_INTEGRITY);
    } else if (!hasCrossorigin) {
      createViolation(node, MSG_MISSING_CROSSORIGIN);
    }
  }

  private static boolean hasIntegrityRelevantRel(TagNode node) {
    Attribute rel = node.getProperty("rel");
    return rel != null && LINK_REL_VALUES.contains(rel.getValue().toLowerCase(Locale.ROOT));
  }

  private static boolean isExternal(String url) {
    return url.startsWith("http") || url.startsWith("//");
  }

  private static boolean hasVersionInUrl(String url) {
    return VERSION_PATTERN.matcher(url).find();
  }

  private static boolean hasCrossoriginAnonymous(TagNode node) {
    Attribute crossorigin = node.getProperty("crossorigin");
    return crossorigin != null && "anonymous".equalsIgnoreCase(crossorigin.getValue());
  }

}
