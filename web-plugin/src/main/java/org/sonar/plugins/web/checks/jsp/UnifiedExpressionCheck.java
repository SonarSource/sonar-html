/*
 * Copyright (C) 2010 Matthijs Galesloot
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

package org.sonar.plugins.web.checks.jsp;

import javax.el.ELException;

import org.jboss.seam.core.Expressions;
import org.jboss.seam.core.Expressions.ValueExpression;
import org.sonar.check.Check;
import org.sonar.check.IsoCategory;
import org.sonar.check.Priority;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.checks.Utils;
import org.sonar.plugins.web.node.Attribute;
import org.sonar.plugins.web.node.TagNode;

/**
 * Checker to find hardcoded labels and messages.
 * 
 * @author Matthijs Galesloot
 * @since 1.0
 */
@Check(key = "UnifiedExpressionCheck", title = "Invalid Expression", description = "Invalid expressions syntax",
    priority = Priority.BLOCKER, isoCategory = IsoCategory.Reliability)
public class UnifiedExpressionCheck extends AbstractPageCheck {

  @Override
  public void startElement(TagNode element) {

    for (Attribute attribute : element.getAttributes()) {
      String value = attribute.getValue();
      if (value != null) {
        value = value.trim();
        if (value.length() > 0 && Utils.isUnifiedExpression(value)) {
          validateExpression(element, attribute.getName(), value);
        }
      }
    }
  }

  private void validateExpression(TagNode element, String attribute, String value) {

    Expressions expressions = Expressions.instance();

    try {
      if ("onclick".equals(attribute)) {
        expressions.createMethodExpression(value);
      } else {
        ValueExpression<Object> ve = expressions.createValueExpression(value);
        ve.toUnifiedValueExpression();
      }
    } catch (ELException e) {

      if (e.getMessage().startsWith("Error")) {
        createViolation(element.getStartLinePosition(), getRule().getDescription() + ": " + e.getMessage());
        // System.out.println(attribute + ">>" + value + " "+ e.getMessage());
      }
    }
  }
}