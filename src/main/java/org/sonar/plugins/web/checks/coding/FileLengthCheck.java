/*
 * SonarQube Web Plugin
 * Copyright (C) 2010 SonarSource and Matthijs Galesloot
 * dev@sonar.codehaus.org
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
package org.sonar.plugins.web.checks.coding;

import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.checks.RuleTags;
import org.sonar.plugins.web.checks.WebRule;

@Rule(
  key = "FileLengthCheck",
  priority = Priority.MINOR)
@WebRule(activeByDefault = false)
@RuleTags({
  RuleTags.BRAIN_OVERLOADED
})
public class FileLengthCheck extends AbstractPageCheck {

  private static final int DEFAULT_MAX_FILE_LENGTH = 500;

  @RuleProperty(
    key = "maxLength",
    defaultValue = "" + DEFAULT_MAX_FILE_LENGTH)
  public int maxLength = DEFAULT_MAX_FILE_LENGTH;

  @Override
  public void endDocument() {
    Measure lines = getWebSourceCode().getMeasure(CoreMetrics.LINES);
    if (lines != null && lines.getIntValue() > maxLength) {
      createViolation(0, "Current file length (" + lines.getIntValue() + ") exceeds the maximum threshold set to " + maxLength);
    }
  }

}
