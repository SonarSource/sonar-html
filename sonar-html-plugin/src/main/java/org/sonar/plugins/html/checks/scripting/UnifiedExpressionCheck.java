/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (C) 2010-2024 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package org.sonar.plugins.html.checks.scripting;

import jakarta.el.ELContext;
import jakarta.el.ELException;
import jakarta.el.ELResolver;
import jakarta.el.ExpressionFactory;
import jakarta.el.FunctionMapper;
import jakarta.el.VariableMapper;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Attribute;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "UnifiedExpressionCheck")
public class UnifiedExpressionCheck extends AbstractPageCheck {

  private static final String DEFAULT_FUNCTIONS = "";
  private static final Set<String> JSTL_FUNCTIONS = Set.of(
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
  );

  /**
   * List of supported functions. Use of unknown functions raises a violation.
   * @since 1.1
   */
  @RuleProperty(
    key = "functions",
    description = "Comma-separated list of names of functions",
    defaultValue = DEFAULT_FUNCTIONS)
  public String functions = DEFAULT_FUNCTIONS;

  private Set<String> functionsSet = new HashSet<>();

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
      if (functions.isEmpty()) {
        return null;
      } else {
        return new FunctionMapper() {

          @Override
          public Method resolveFunction(String prefix, String localName) {
            if (!JSTL_FUNCTIONS.contains(localName) && !functionsSet.contains(localName)) {
              createViolation(element.getStartLinePosition(), "Fix this expression: Unknown function \"" + localName + "\".");
            }

            // we only care about the check.
            return null;
          }

        };
      }
    }

    @Override
    public VariableMapper getVariableMapper() {
      return null;
    }
  }

  @Override
  public void startDocument(List<Node> nodes) {
    functionsSet = Arrays.stream(functions.split(",")).map(String::strip).collect(Collectors.toSet());
  }

  @Override
  public void startElement(TagNode element) {

    for (Attribute attribute : element.getAttributes()) {
      String name = attribute.getName();
      String value = attribute.getValue();

      // Ignore thymeleaf expressions
      if (!name.startsWith("th:") && value != null) {
        value = value.trim();
        if (value.length() > 0 && isUnifiedExpression(value)) {
          validateExpression(element, attribute);
        }
      }
    }
  }

  private void validateExpression(TagNode element, Attribute attribute) {
    ExpressionLanguageContext context = new ExpressionLanguageContext(element);

    try {
      ExpressionFactory expressionFactory = ExpressionFactory.newInstance();
      expressionFactory.createValueExpression(context, attribute.getValue(), Object.class);
    } catch (ELException e) {
      if (e.getMessage().startsWith("Error")) {
        createViolation(element.getStartLinePosition(), "Fix this expression: " + (e.getMessage() == null ? "" : e.getMessage()));
      }
    }
  }

}
