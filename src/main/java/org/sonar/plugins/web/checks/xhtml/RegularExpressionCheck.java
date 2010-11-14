/*
 * Sonar Web Plugin
 * Copyright (C) 2010 Matthijs Galesloot
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

package org.sonar.plugins.web.checks.xhtml;


import java.util.regex.Pattern;

import org.sonar.check.IsoCategory;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.node.Attribute;
import org.sonar.plugins.web.node.TagNode;

/**
 * Checker for RegularExpressions.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
@Rule(key = "RegularExpressionCheck", name ="Regular Expression Check", description = "Regular Expression Check",
    priority = Priority.MINOR, isoCategory = IsoCategory.Maintainability)
public class RegularExpressionCheck extends AbstractPageCheck {

  @RuleProperty(key = "expression")
  private String expression;

  @RuleProperty(key = "scope")
  private String scope; // attribute or element

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
            createViolation(element.getStartLinePosition(), getRule().getDescription() + ": " + a.getValue());
          }
        }
      } else {
        if (pattern.matcher(element.getCode()).lookingAt()) {
          createViolation(element.getStartLinePosition(), getRule().getDescription());
        }
      }
    }
  }
}