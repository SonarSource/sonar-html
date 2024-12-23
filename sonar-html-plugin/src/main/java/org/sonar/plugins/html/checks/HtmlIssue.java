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

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.sonar.api.rule.RuleKey;

public class HtmlIssue {

  private final RuleKey ruleKey;
  private final Integer line;
  private final String message;
  private final Double cost;

  public HtmlIssue(RuleKey ruleKey, @Nullable Integer line, String message) {
    this.ruleKey = ruleKey;
    this.line = line;
    this.message = message;
    this.cost = null;
  }

  public HtmlIssue(RuleKey ruleKey, @Nullable Integer line, String message, double cost) {
    if (cost <= 0) {
      throw new IllegalArgumentException("Cost cannot be <= 0");
    }

    this.ruleKey = ruleKey;
    this.line = line;
    this.message = message;
    this.cost = cost;
  }

  public RuleKey ruleKey() {
    return ruleKey;
  }

  @CheckForNull
  public Integer line() {
    return line;
  }

  public String message() {
    return message;
  }

  public Double cost() {
    return cost;
  }

}
