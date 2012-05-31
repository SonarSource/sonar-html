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

package org.sonar.plugins.web.checks.header;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.node.CommentNode;
import org.sonar.plugins.web.node.TagNode;
import org.sonar.plugins.web.visitor.WebSourceCode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Header checker.
 *
 * @see http://java.sun.com/developer/technicalArticles/javaserverpages/code_convention/ paragraph Opening Comments
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
@Rule(key = "HeaderCheck", priority = Priority.MAJOR)
public class HeaderCheck extends AbstractPageCheck {

  private static final Logger LOG = LoggerFactory.getLogger(HeaderCheck.class);

  @RuleProperty(defaultValue = "^.*Copyright.*$")
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
        LOG.debug("Header is not in valid format");
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
      if (!hasHeader) {
        createViolation(0);
      }
      visiting = false;
    }
  }

}
