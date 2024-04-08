/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (c) 2010-2024 SonarSource SA and Matthijs Galesloot
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
package org.sonar.plugins.html.checks.coding;

import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.html.checks.AbstractPageCheck;
@Rule(key = "FileLengthCheck")
public class FileLengthCheck extends AbstractPageCheck {

  private static final int DEFAULT_MAX_FILE_LENGTH = 1000;

  @RuleProperty(
    key = "maxLength",
    description = "Maximum authorized lines of code in a file.",
    defaultValue = "" + DEFAULT_MAX_FILE_LENGTH)
  public int maxLength = DEFAULT_MAX_FILE_LENGTH;

  @Override
  public void endDocument() {
    int loc = getHtmlSourceCode().getDetailedLinesOfCode().size();
    if (loc > maxLength) {
      createViolation(0, "Current file has " + loc + " lines, which is greater than " + maxLength + " authorized. Split it into smaller files.");
    }
  }

}
