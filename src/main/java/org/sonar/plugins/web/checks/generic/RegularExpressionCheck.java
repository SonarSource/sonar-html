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
import org.sonar.plugins.web.node.TagNode;

import java.util.regex.Pattern;

/**
 * Checker for RegularExpressions.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
@Rule(key = "RegularExpressionCheck", priority = Priority.MINOR, cardinality = Cardinality.MULTIPLE)
public class RegularExpressionCheck extends AbstractPageCheck {

  @RuleProperty
  private String expression;

  // value must be "attribute" or "element"
  @RuleProperty
  private String scope;

  public String getScope() {
    return scope;
  }

  public void setScope(String scope) {
    this.scope = scope;
  }

  private Pattern pattern;

  public String getExpression() {
    return expression;
  }

  public void setExpression(String expression) {

    this.expression = expression;
    this.pattern = Pattern.compile(expression, Pattern.MULTILINE);
  }

  @Override
  public void startElement(TagNode element) {

    if (pattern != null) {
      // scope is attribute or element
      if ("attribute".equals(scope)) {
        for (Attribute a : element.getAttributes()) {
          if (pattern.matcher(a.getValue()).lookingAt()) {
            createViolation(element.getStartLinePosition(), "The value of this attribute ('" + a.getValue() + "') does not match the given regular expression: " + expression);
          }
        }
      } else {
        if (pattern.matcher(element.getCode()).lookingAt()) {
          createViolation(element.getStartLinePosition(), "The text of this element ('" + element.getNodeName() + "') does not match the given regular expression: " + expression);
        }
      }
    }
  }
}
