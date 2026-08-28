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
package org.sonar.plugins.html.checks.accessibility;

import org.sonar.check.Rule;
import org.sonar.plugins.html.api.Helpers;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "S9379")
public class NoAutofocusCheck extends AbstractPageCheck {

  private static final String MESSAGE = "Remove this \"autofocus\" attribute.";

  @Override
  public void startElement(TagNode node) {
    if (!node.hasProperty("autofocus")) {
      return;
    }
    if (Helpers.hasAncestorMatching(node, NoAutofocusCheck::isExemptAncestor)) {
      return;
    }
    createViolation(node, MESSAGE);
  }

  private static boolean isExemptAncestor(TagNode ancestor) {
    return "dialog".equalsIgnoreCase(ancestor.getNodeName()) || ancestor.hasProperty("popover");
  }

}
