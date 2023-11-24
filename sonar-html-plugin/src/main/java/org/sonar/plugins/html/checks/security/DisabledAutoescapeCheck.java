/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (c) 2010-2023 SonarSource SA and Matthijs Galesloot
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
package org.sonar.plugins.html.checks.security;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TextNode;

/*
 Note: While this rule covers Jinja2/Django templates, the HTML analyzer is not meant to explicitly support these templating engine and will only cover basic cases.
 Any more elaborate support of these engines should be done in a dedicated sensor with a dedicated parser.
 */
@Rule(key = "S5247")
public class DisabledAutoescapeCheck extends AbstractPageCheck {

  private static final String MESSAGE = "Make sure disabling auto-escaping feature is safe here.";
  private static final Pattern pattern = Pattern.compile("\\{%\\s*autoescape\\s+(false|off)\\s*%\\}|\\|safe\\s*\\}\\}");

  @Override
  public void characters(TextNode textNode) {
    Matcher matcher = pattern.matcher(textNode.getCode());
    if (!matcher.find()) {
      return;
    }
    var lines = textNode.getCode().lines()
        .collect(Collectors.toList());
    boolean raisedWithPreciseLocation = false;
    for (int i=0; i<lines.size(); i++) {
      Matcher lineMatcher = pattern.matcher(lines.get(i));
      while (lineMatcher.find()) {
        int issueLine = i + textNode.getStartLinePosition();
        int startColumn = i != 0 ? lineMatcher.start() : (textNode.getStartColumnPosition() + lineMatcher.start());
        int endColumn = i != 0 ? lineMatcher.end() : (textNode.getStartColumnPosition() + lineMatcher.end());
        createViolation(issueLine, startColumn, issueLine, endColumn, MESSAGE);
        raisedWithPreciseLocation = true;
      }
    }
    if (!raisedWithPreciseLocation) {
      // If no precise location can be found for the issue (the node only contains violations spanning multiple lines), the issue is raised on the whole text node
      createViolation(textNode, MESSAGE);
    }
  }
}
