/*
 * Sonar Web Plugin
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
package org.sonar.plugins.web.checks.generic;

import org.sonar.check.Cardinality;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.node.Attribute;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.node.TagNode;

import java.util.List;
import java.util.regex.Pattern;

@Rule(
  key = "RegularExpressionCheck",
  priority = Priority.MAJOR,
  cardinality = Cardinality.MULTIPLE)
public class RegularExpressionCheck extends AbstractPageCheck {

  private static final String DEFAULT_EXPRESSION = "";
  private static final String DEFAULT_SCOPE = "";

  @RuleProperty(
    key = "expression",
    defaultValue = DEFAULT_EXPRESSION)
  public String expression = DEFAULT_EXPRESSION;

  @RuleProperty(
    key = "scope",
    defaultValue = DEFAULT_SCOPE)
  public String scope = DEFAULT_SCOPE;

  private Pattern pattern;

  @Override
  public void startDocument(List<Node> nodes) {
    pattern = Pattern.compile(expression, Pattern.MULTILINE);
  }

  @Override
  public void startElement(TagNode element) {
    if (!expression.isEmpty()) {
      if ("attribute".equals(scope)) {
        for (Attribute a : element.getAttributes()) {
          if (pattern.matcher(a.getValue()).lookingAt()) {
            createViolation(element.getStartLinePosition(), "The value of this attribute '" + a.getValue() + "' matches the given regular expression: " + expression);
          }
        }
      } else {
        if (pattern.matcher(element.getCode()).lookingAt()) {
          createViolation(element.getStartLinePosition(), "The text of this element '" + element.getCode() + "' matches the given regular expression: " + expression);
        }
      }
    }
  }

}
