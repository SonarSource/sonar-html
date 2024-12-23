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
package org.sonar.plugins.html.checks;

import org.sonar.api.rule.RuleKey;

public class PreciseHtmlIssue extends HtmlIssue {

  private final int startColumn;
  private final int endColumn;
  private final int endLine;

  PreciseHtmlIssue(RuleKey ruleKey, int line, String message, int startColumn, int endLine, int endColumn) {
    super(ruleKey, line, message);
    this.startColumn = startColumn;
    this.endLine = endLine;
    this.endColumn = endColumn;
  }

  public int startColumn() {
    return startColumn;
  }

  public int endLine() {
    return endLine;
  }

  public int endColumn() {
    return endColumn;
  }
}
