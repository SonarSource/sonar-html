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
package org.sonar.plugins.html.checks.sonar;


import java.util.regex.Pattern;

import org.sonar.check.Rule;
import org.sonar.plugins.html.api.Helpers;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;


@Rule(key = "S8697")
public class SrcSetDescriptorCheck extends AbstractPageCheck {
  // Pattern that describes what a valid descriptor is:
  // - Width descriptor: An integer followed by 'w' (e.g. 400w)
  // - Pixel density  descriptor: A floating-point followed by 'x' (e.g 1.5x)
  private static final Pattern VALID_DESCRIPTOR = Pattern.compile(
      "\\s+(\\d+w|\\d+(\\.\\d+)?x)$"
  );
  
  @Override
  public void startElement(TagNode node) {
    if (!this.isTargetedNode(node)) {
      return;
    }

    // Read the value in srcset
    String srcSetValue = node.getAttribute("srcset");

    if (srcSetValue == null) {
      return;
    }
    
    if (Helpers.isDynamicValue(srcSetValue, getHtmlSourceCode())) {
      return;
    }

    // Split the value by comma so we can extract the difference sources
    String[] sources = srcSetValue.split(",");

    if (sources.length < 2) {
      return;
    }

    for (String source: sources) {
      String sanitizedSource = source.trim();
      if (!VALID_DESCRIPTOR.matcher(sanitizedSource).find()) {
        createViolation(node, "Element \"" + node.getNodeName() + "\" has no valid and explicit descriptor.");
        return;
      }
    }
  }

  /**
   * Tells if a node is targeted by this rule or not
   * @param node node to check
   * @return true if node is concerned by rule S8697, false otherwise
   */
  private boolean isTargetedNode(TagNode node) {
    if (!this.isImageNode(node) && !this.isSourceNode(node)) {
      return false;
    }
    if (!node.hasAttribute("srcset")) {
      return false;
    }
    return true;
  }

  private boolean isImageNode(TagNode node) {
    return "img".equalsIgnoreCase(node.getNodeName());
  }

  private boolean isSourceNode(TagNode node) {
    return "source".equalsIgnoreCase(node.getNodeName());
  }
}
