/*
 * SonarQube Web Plugin
 * Copyright (C) 2010 SonarSource and Matthijs Galesloot
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
package org.sonar.plugins.web.checks;

import org.sonar.api.rule.RuleKey;

import com.google.common.base.Preconditions;

public class WebIssue {

  private final RuleKey ruleKey;
  private final Integer line;
  private final String message;
  private final Double cost;

  public WebIssue(RuleKey ruleKey, Integer line, String message) {
    this.ruleKey = ruleKey;
    this.line = line;
    this.message = message;
    this.cost = null;
  }

  public WebIssue(RuleKey ruleKey, Integer line, String message, Double cost) {
    Preconditions.checkArgument(cost > 0);

    this.ruleKey = ruleKey;
    this.line = line;
    this.message = message;
    this.cost = cost;
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

  public boolean hasEffortToFix() {
    return cost != null;
  }

  @Override
  public String toString() {
    return "WebIssue{" +
      "ruleKey=" + ruleKey +
      ", line=" + line +
      ", message='" + message + '\'' +
      '}';
  }

}
