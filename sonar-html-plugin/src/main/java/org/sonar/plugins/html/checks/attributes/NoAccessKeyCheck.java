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
package org.sonar.plugins.html.checks.attributes;

import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "S6846")
public class NoAccessKeyCheck extends AbstractPageCheck {

  private static final String MESSAGE = "No access key attribute allowed. Inconsistencies between keyboard shortcuts and keyboard commands used by screenreaders and keyboard-only users create a11y complications.";
  private static final String ATTRIBUTE = "accessKey";

  @Override
  public void startElement(TagNode element) {
    if (element.hasProperty(ATTRIBUTE)) {
      var start = element.getStartColumnPosition() + 1;
      createViolation(
        element.getStartLinePosition(),
        start,
        element.getStartLinePosition(),
        start + element.getNodeName().length(),
        MESSAGE);
    }
  }
}
