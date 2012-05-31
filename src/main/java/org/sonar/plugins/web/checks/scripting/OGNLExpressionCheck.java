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

import ognl.Ognl;
import ognl.OgnlException;
import org.apache.commons.lang.StringUtils;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.node.Attribute;
import org.sonar.plugins.web.node.TagNode;

/**
 * Checker to validate OGNL Expressions.
 *
 * @author Matthijs Galesloot
 * @since 1.1
 */
@Rule(key = "OGNLExpressionCheck", name = "Invalid OGNL Expression", description = "Invalid expressions syntax",
  priority = Priority.BLOCKER)
public class OGNLExpressionCheck extends AbstractPageCheck {

  @Override
  public void startElement(TagNode element) {

    for (Attribute attribute : element.getAttributes()) {
      String value = attribute.getValue();
      if (value != null) {
        parseAndValidate(element, value);
      }
    }
  }

  private void parseAndValidate(TagNode element, String text) {
    for (int i = 0; i + 1 < text.length(); i++) {
      if ((text.charAt(i) == '%' || text.charAt(i) == '#') && text.charAt(i + 1) == '{') {
        String expression = StringUtils.substringBeforeLast(StringUtils.substring(text, i + 2), "}");
        validateExpression(element, expression);
      }
    }
  }

  private void validateExpression(TagNode element, String value) {
    try {
      Ognl.parseExpression(value);
    } catch (OgnlException e) {
      createViolation(element.getStartLinePosition(), getRule().getDescription() + ": " + e.getMessage());
    }
  }
}
