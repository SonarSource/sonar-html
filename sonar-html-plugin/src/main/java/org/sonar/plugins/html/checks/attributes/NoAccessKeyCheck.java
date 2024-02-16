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
package org.sonar.plugins.html.checks.attributes;

import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "S6846")
public class NoAccessKeyCheck extends AbstractPageCheck {

  private static final String MESSAGE = "No access key attribute allowed. Inconsistencies between keyboard shortcuts and keyboard commands used by screenreaders and keyboard-only users create a11y complications.";
  private static final String ATTRIBUTE = "accessKey";

  @Override
  public void startElement(TagNode element) {
    if (element.hasProperty(ATTRIBUTE)) {
      var start = element.getStartColumnPosition() + 1;
      createViolation(
        element.getStartLinePosition(),
        start,
        element.getStartLinePosition(),
        start + element.getNodeName().length(),
        MESSAGE);
    }
  }
}
