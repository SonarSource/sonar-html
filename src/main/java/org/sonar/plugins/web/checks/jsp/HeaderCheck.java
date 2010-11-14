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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.check.IsoCategory;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.node.CommentNode;
import org.sonar.plugins.web.node.TagNode;
import org.sonar.plugins.web.visitor.WebSourceCode;

/**
 * Header checker.
 *
 * @see http://java.sun.com/developer/technicalArticles/javaserverpages/code_convention/ paragraph Opening Comments
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
@Rule(key = "HeaderCheck", name = "Missing Header", description = "Missing header comments", priority = Priority.MAJOR,
    isoCategory = IsoCategory.Maintainability)
public class HeaderCheck extends AbstractPageCheck {

  private static final Logger LOG = LoggerFactory.getLogger(HeaderCheck.class);

  @RuleProperty(key = "expression", defaultValue = "^.*Copyright.*$", description = "Regular expression for header format")
  private String expression;

  private boolean hasHeader;
  private Matcher matcher;
  private boolean visiting;

  @Override
  public void comment(CommentNode node) {

    if (visiting) {
      if (matchHeader(node.getCode())) {
        hasHeader = true;
      } else {
        createViolation(0, "Header is not in correct format");
        LOG.warn("Header is not in valid format");
      }
    }
    visiting = false;
  }

  public String getExpression() {
    return expression;
  }

  private boolean matchHeader(String header) {
    if (matcher == null) {
      Pattern pattern = Pattern.compile(expression, Pattern.MULTILINE);
      matcher = pattern.matcher(header);
    } else {
      matcher.reset(header);
    }
    return matcher.find();
  }

  public void setExpression(String expression) {
    this.expression = expression;
  }

  @Override
  public void startDocument(WebSourceCode webSourceCode) {
    super.startDocument(webSourceCode);
    hasHeader = false;
    visiting = true;
  }

  @Override
  public void startElement(TagNode node) {
    if (visiting) {
      if ( !hasHeader) {
        createViolation(0);
      }
      visiting = false;
    }
  }

}