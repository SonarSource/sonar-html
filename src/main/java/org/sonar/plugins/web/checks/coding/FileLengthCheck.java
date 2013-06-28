/*
 * Sonar Web Plugin
 * Copyright (C) 2010 Matthijs Galesloot and SonarSource
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

/**
 * Checker for length of file.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
@Rule(key = "FileLengthCheck", priority = Priority.MINOR)
public class FileLengthCheck extends AbstractPageCheck {

  private static final int DEFAULT_MAX_FILE_LENGTH = 500;

  @RuleProperty(defaultValue = "500")
  private int maxLength = DEFAULT_MAX_FILE_LENGTH;

  public int getMaxLength() {
    return maxLength;
  }

  public void setMaxLength(int maxLength) {
    this.maxLength = maxLength;
  }

  @Override
  public void endDocument() {
    Measure lines = getWebSourceCode().getMeasure(CoreMetrics.LINES);
    if (lines != null && lines.getIntValue() > maxLength) {
      createViolation(0, "Current file length (" + lines.getIntValue() + ") exceeds the maximum threshold set to " + maxLength);
    }
    super.endDocument();
  }
}
