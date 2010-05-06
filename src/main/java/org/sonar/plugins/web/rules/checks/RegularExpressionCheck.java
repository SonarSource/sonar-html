/*
 * Copyright (C) 2010
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

package org.sonar.plugins.web.rules.checks;

import java.util.regex.Pattern;

import org.sonar.plugins.web.WebUtils;
import org.sonar.plugins.web.lex.HtmlElement;
import org.sonar.plugins.web.lex.Token;

/**
 * Experimental checker for RegularExpressions
 * 
 * @author Matthijs Galesloot
 */
public class RegularExpressionCheck extends HtmlCheck {

  private String expression;

  private Pattern pattern;

  public String getExpression() {
    return expression;
  }

  public void setExpression(String expression) {

    this.expression = expression;
    this.pattern = Pattern.compile(expression, Pattern.MULTILINE);
  }

  @Override
  public void startElement(Token token) {

    if (token instanceof HtmlElement) {
      if (pattern.matcher(token.getCode()).find()) {

        WebUtils.LOG.debug("Invalid token found: " + token.getCode());
        createViolation(token);
      }
    }
  }
}