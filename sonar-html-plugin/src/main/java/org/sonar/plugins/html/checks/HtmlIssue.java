/*
 * SonarHTML :: SonarQube Plugin
 * Copyright (c) 2010-2019 SonarSource SA and Matthijs Galesloot
 * sonarqube@googlegroups.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sonar.plugins.html.checks;

import javax.annotation.Nullable;
import org.sonar.api.rule.RuleKey;

public class HtmlIssue {

  private final RuleKey ruleKey;
  private final Integer line;
  private final String message;
  private final Double cost;
  private final Integer startColumn;
  private final Integer endColumn;
  private final Integer endLine;

  public HtmlIssue(RuleKey ruleKey, Integer line, String message) {
    this(ruleKey, line, message, null);
  }

  public HtmlIssue(RuleKey ruleKey, Integer line, String message, @Nullable Double cost) {
    this(ruleKey, line, message, cost, null, null, null);
  }

  public HtmlIssue(RuleKey ruleKey, Integer line, String message, @Nullable Double cost, @Nullable Integer startColumn,
                   @Nullable Integer endLine, @Nullable Integer endColumn) {
    if (cost != null && cost <= 0) {
      throw new IllegalArgumentException("Cost cannot be <= 0");
    }
    this.ruleKey = ruleKey;
    this.line = line;
    this.message = message;
    this.cost = cost;
    this.startColumn = startColumn;
    this.endColumn = endColumn;
    this.endLine = endLine;
  }

  public RuleKey ruleKey() {
    return ruleKey;
  }

  public Integer line() {
    return line;
  }

  public String message() {
    return message;
  }

  public Double cost() {
    return cost;
  }

  public Integer endLine() {
    return endLine;
  }

  public Integer startColumn() {
    return startColumn;
  }

  public Integer endColumn() {
    return endColumn;
  }

}
