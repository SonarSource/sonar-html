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
package org.sonar.plugins.html.checks.whitespace;

import java.io.IOException;
import java.util.List;
import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Node;

@Rule(key = "IllegalTabCheck")
public class IllegalTabCheck extends AbstractPageCheck {

  @Override
  public void startDocument(List<Node> nodes) {
    String content;
    try {
      content = getHtmlSourceCode().inputFile().contents();
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
    String[] lines = content.split("\\r?\\n");
    for (int i = 0; i < lines.length; i++) {
      if (lines[i].contains("\t")) {
        createViolation(i + 1, "Replace all tab characters in this file by sequences of white-spaces.");
        break;
      }
    }
  }
}
