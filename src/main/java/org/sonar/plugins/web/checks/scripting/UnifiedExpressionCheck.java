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

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.jboss.el.lang.ExpressionBuilder;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.node.Attribute;
import org.sonar.plugins.web.node.TagNode;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.FunctionMapper;
import javax.el.VariableMapper;

import java.lang.reflect.Method;

/**
 * Checker to validate Unified Expressions in JSF.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
@Rule(key = "UnifiedExpressionCheck", priority = Priority.BLOCKER)
public class UnifiedExpressionCheck extends AbstractPageCheck {

  /**
   * List of supported functions. Use of unknown functions raises a violation.
   * @since 1.1
   */
  @RuleProperty
  private String[] functions;

  public void setFunctions(String list) {
    functions = StringUtils.stripAll(StringUtils.split(list, ","));
  }

  private static final String[] JSTL_FUNCTIONS = new String[] {
    "contains",
    "containsIgnoreCase",
    "endsWith",
    "escapeXml",
    "indexOf",
    "join",
    "length",
    "replace",
    "split",
    "startsWith",
    "substring",
    "substringAfter",
    "substringBefore",
    "toLowerCase",
    "toUpperCase",
    "trim"
  };

  public String getFunctions() {
    if (functions != null) {
      return StringUtils.join(functions, ",");
    }
    return "";
  }

  /**
   * ELContext for use by ExpressionBuilder.
   */
  private class ExpressionLanguageContext extends ELContext {

    private final TagNode element;

    public ExpressionLanguageContext(TagNode element) {
      this.element = element;
    }

    @Override
    public ELResolver getELResolver() {
      return null;
    }

    @Override
    public FunctionMapper getFunctionMapper() {
      if (functions == null) {
        return null;
      } else {
        return new FunctionMapper() {

          @Override
          public Method resolveFunction(String prefix, String localName) {

            if (!ArrayUtils.contains(JSTL_FUNCTIONS, localName) && !ArrayUtils.contains(functions, localName)) {
              createViolation(element.getStartLinePosition(), "Unknown function: " + localName);
            }

            return null; // we only care about the check.
          }
        };
      }
    }

    @Override
    public VariableMapper getVariableMapper() {
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
          validateExpression(element, attribute);
        }
      }
    }
  }

  private void validateExpression(TagNode element, Attribute attribute) {
    ExpressionLanguageContext context = new ExpressionLanguageContext(element);
    ExpressionBuilder builder = new ExpressionBuilder(attribute.getValue(), context);

    try {
      builder.createValueExpression(Object.class);
    } catch (ELException e) {
      if (e.getMessage().startsWith("Error")) {
        createViolation(element.getStartLinePosition(), "This expression is not valid. " + (e.getMessage() == null ? "" : e.getMessage()));
      }
    }
  }
}
