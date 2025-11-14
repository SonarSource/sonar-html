/*
 * SonarQube HTML
 * Copyright (C) 2010-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource Sàrl.
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
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;

import static org.sonar.plugins.html.api.HtmlConstants.hasKnownHTMLTag;
import static org.sonar.plugins.html.api.HtmlConstants.hasNonInteractiveRole;
import static org.sonar.plugins.html.api.HtmlConstants.hasPresentationRole;
import static org.sonar.plugins.html.api.HtmlConstants.isInteractiveElement;

@Rule(key = "S6843")
public class NoInteractiveElementToNoninteractiveRoleCheck extends AbstractPageCheck {

  private static final String MESSAGE = "Interactive elements should not be assigned non-interactive roles.";

  @Override
  public void startElement(TagNode node) {
    if (
      hasKnownHTMLTag(node) &&
      isInteractiveElement(node) &&
      (hasNonInteractiveRole(node) || hasPresentationRole(node))
    ) {
      createViolation(node, MESSAGE);
    }
  }

}
