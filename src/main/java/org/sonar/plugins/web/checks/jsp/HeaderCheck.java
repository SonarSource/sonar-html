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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sonar.check.Check;
import org.sonar.check.CheckProperty;
import org.sonar.check.IsoCategory;
import org.sonar.check.Priority;
import org.sonar.plugins.web.WebUtils;
import org.sonar.plugins.web.node.CommentNode;
import org.sonar.plugins.web.node.TagNode;
import org.sonar.plugins.web.rules.AbstractPageCheck;
import org.sonar.plugins.web.visitor.WebSourceCode;

/**
 * Header checker for JSP files.
 * 
 * @see http://java.sun.com/developer/technicalArticles/javaserverpages/code_convention/
 * 
 * @author Matthijs Galesloot
 */
@Check(key = "HeaderCheck", title = "Missing Header", description = "Missing header comments", priority = Priority.MAJOR, isoCategory = IsoCategory.Maintainability)
public class HeaderCheck extends AbstractPageCheck {

  @CheckProperty(key = "expression", title = "Regular Expression", description = "Regular expression for header format")
  private String expression = "^.*Copyright.*$";
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
        WebUtils.LOG.warn("Header is not in valid format");
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