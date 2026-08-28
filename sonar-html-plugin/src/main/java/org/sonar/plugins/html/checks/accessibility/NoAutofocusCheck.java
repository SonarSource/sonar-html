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

import java.util.Arrays;
import org.sonar.check.Rule;
import org.sonar.plugins.html.api.Helpers;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "S9379")
public class NoAutofocusCheck extends AbstractPageCheck {

  private static final String MESSAGE = "Remove this \"autofocus\" attribute, as it can reduce usability and accessibility for users.";

  @Override
  public void startElement(TagNode node) {
    if (!node.hasProperty("autofocus")) {
      return;
    }
    if (isDialogOrPopover(node) || Helpers.hasAncestorMatching(node, NoAutofocusCheck::isDialogOrPopover)) {
      return;
    }
    createViolation(node, MESSAGE);
  }

  private static boolean isDialogOrPopover(TagNode node) {
    if ("dialog".equalsIgnoreCase(node.getNodeName()) || node.hasProperty("popover")) {
      return true;
    }
    String role = node.getAttribute("role");
    if (role == null) {
      // static role absent: a bound role (:role/[role]) cannot be resolved, do not report
      return node.getProperty("role") != null;
    }
    return Arrays.stream(role.trim().split("\\s+"))
      .anyMatch(token -> "dialog".equalsIgnoreCase(token) || "alertdialog".equalsIgnoreCase(token));
  }

}
