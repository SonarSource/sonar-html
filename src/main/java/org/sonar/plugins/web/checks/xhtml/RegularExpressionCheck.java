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