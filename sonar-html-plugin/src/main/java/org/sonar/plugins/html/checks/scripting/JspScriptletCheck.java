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
package org.sonar.plugins.html.checks.scripting;

import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.ExpressionNode;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "JspScriptletCheck")
public class JspScriptletCheck extends AbstractPageCheck {

  private static final String SCRIPTLET_PREFIX = "<%";
  private static final String SCRIPTLET_SUFFIX = "%>";

  @Override
  public void expression(ExpressionNode node) {
    String content = trimScriptlet(node.getCode());

    if (!content.isBlank()) {
      createIssue(node.getStartLinePosition());
    }
  }

  @Override
  public void startElement(TagNode element) {
    if ("scriptlet".equalsIgnoreCase(element.getLocalName())) {
      createIssue(element.getStartLinePosition());
    }
  }

  private static String trimScriptlet(String code) {
    if (code.startsWith(SCRIPTLET_PREFIX)){
      code = code.substring(SCRIPTLET_PREFIX.length());
    }
    if (code.endsWith(SCRIPTLET_SUFFIX)){
      code = code.substring(0, code.length() - SCRIPTLET_SUFFIX.length());
    }
    return code;
  }

  private void createIssue(int line) {
    createViolation(line, "Replace this scriptlet using tag libraries and expression language.");
  }

}
