/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (c) 2010-2022 SonarSource SA and Matthijs Galesloot
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
