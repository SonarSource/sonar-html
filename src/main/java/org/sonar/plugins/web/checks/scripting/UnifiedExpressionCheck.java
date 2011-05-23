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

package org.sonar.plugins.web.checks.scripting;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.FunctionMapper;
import javax.el.VariableMapper;

import org.jboss.el.lang.ExpressionBuilder;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.node.Attribute;
import org.sonar.plugins.web.node.TagNode;

/**
 * Checker to find hardcoded labels and messages.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
@Rule(key = "UnifiedExpressionCheck", name = "Invalid Expression", description = "Invalid expressions syntax", priority = Priority.BLOCKER)
public class UnifiedExpressionCheck extends AbstractPageCheck {

  /**
   * ELContext for use by ExpressionBuilder.
   */
  private final ELContext ctx = new ELContext() {

    @Override
    public ELResolver getELResolver() {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public FunctionMapper getFunctionMapper() {
      return null;
    }

    @Override
    public VariableMapper getVariableMapper() {
      // TODO Auto-generated method stub
      return null;
    }
  };

  @Override
  public void startElement(TagNode element) {

    for (Attribute attribute : element.getAttributes()) {
      String value = attribute.getValue();
      if (value != null) {
        value = value.trim();
        if (value.length() > 0 && isUnifiedExpression(value)) {
          validateExpression(element, attribute.getName(), value);
        }
      }
    }
  }

  private void validateExpression(TagNode element, String attribute, String value) {
    ExpressionBuilder builder = new ExpressionBuilder(value, ctx);

    try {
      builder.createValueExpression(Object.class);
    } catch (ELException e) {

      if (e.getMessage().startsWith("Error")) {
        createViolation(element.getStartLinePosition(), getRule().getDescription() + ": " + e.getMessage());
      }
    }
  }
}