/*
 * SonarQube HTML Plugin :: Sonar Plugin
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
package org.sonar.plugins.html.checks.security;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TextNode;

/*
 Note: While this rule covers Jinja2/Django templates, the HTML analyzer is not meant to explicitly support these templating engine and will only cover basic cases.
 Any more elaborate support of these engines should be done in a dedicated sensor with a dedicated parser.
 */
@Rule(key = "S5247")
public class DisabledAutoescapeCheck extends AbstractPageCheck {

  private static final String MESSAGE = "Make sure disabling auto-escaping feature is safe here.";
  private static final Pattern pattern = Pattern.compile("\\{%\\s*autoescape\\s+(false|off)\\s*%\\}|\\|safe\\s*\\}\\}");

  @Override
  public void characters(TextNode textNode) {
    Matcher matcher = pattern.matcher(textNode.getCode());
    if (!matcher.find()) {
      return;
    }
    var lines = textNode.getCode().lines().toList();
    boolean raisedWithPreciseLocation = false;
    for (int i=0; i<lines.size(); i++) {
      Matcher lineMatcher = pattern.matcher(lines.get(i));
      while (lineMatcher.find()) {
        int issueLine = i + textNode.getStartLinePosition();
        int startColumn = i != 0 ? lineMatcher.start() : (textNode.getStartColumnPosition() + lineMatcher.start());
        int endColumn = i != 0 ? lineMatcher.end() : (textNode.getStartColumnPosition() + lineMatcher.end());
        createViolation(issueLine, startColumn, issueLine, endColumn, MESSAGE);
        raisedWithPreciseLocation = true;
      }
    }
    if (!raisedWithPreciseLocation) {
      // If no precise location can be found for the issue (the node only contains violations spanning multiple lines), the issue is raised on the whole text node
      createViolation(textNode, MESSAGE);
    }
  }
}
