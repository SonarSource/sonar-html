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
package org.sonar.plugins.html.checks.scripting;

import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.ExpressionNode;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "JspScriptletCheck")
public class JspScriptletCheck extends AbstractPageCheck {

  private static final String SCRIPTLET_PREFIX = "<%";
  private static final String SCRIPTLET_SUFFIX = "%>";

  @Override
  public void expression(ExpressionNode node) {
    String content = trimScriptlet(node.getCode());

    if (!content.isBlank()) {
      createIssue(node.getStartLinePosition());
    }
  }

  @Override
  public void startElement(TagNode element) {
    if ("scriptlet".equalsIgnoreCase(element.getLocalName())) {
      createIssue(element.getStartLinePosition());
    }
  }

  private static String trimScriptlet(String code) {
    if (code.startsWith(SCRIPTLET_PREFIX)){
      code = code.substring(SCRIPTLET_PREFIX.length());
    }
    if (code.endsWith(SCRIPTLET_SUFFIX)){
      code = code.substring(0, code.length() - SCRIPTLET_SUFFIX.length());
    }
    return code;
  }

  private void createIssue(int line) {
    createViolation(line, "Replace this scriptlet using tag libraries and expression language.");
  }

}
