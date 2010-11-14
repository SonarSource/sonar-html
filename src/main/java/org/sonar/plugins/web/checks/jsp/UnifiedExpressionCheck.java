/*
 * Sonar Web Plugin
 * Copyright (C) 2010 Matthijs Galesloot
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.plugins.web.checks.jsp;

import javax.el.ELException;

import org.jboss.seam.core.Expressions;
import org.jboss.seam.core.Expressions.ValueExpression;
import org.sonar.check.IsoCategory;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
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
@Rule(key = "UnifiedExpressionCheck", name ="Invalid Expression", description = "Invalid expressions syntax",
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