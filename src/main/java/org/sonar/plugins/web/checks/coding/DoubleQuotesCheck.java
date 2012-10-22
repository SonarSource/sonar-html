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
package org.sonar.plugins.web.checks.coding;

import org.apache.commons.lang.StringUtils;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.node.Attribute;
import org.sonar.plugins.web.node.TagNode;

/**
 * Checker to find use of single quote where double quote is preferred.
 *
 * @see http://java.sun.com/developer/technicalArticles/javaserverpages/code_convention/ paragraph Quoting
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
@Rule(key = "DoubleQuotesCheck", priority = Priority.MINOR)
public class DoubleQuotesCheck extends AbstractPageCheck {

  @Override
  public void startElement(TagNode element) {

    for (Attribute a : element.getAttributes()) {
      if (a.getValue() != null && a.getValue().trim().length() > 0) {
        boolean error = false;
        // single quotes are OK if there are double quotes inside the string
        if (a.isSingleQuoted()) {
          error = !StringUtils.contains(a.getValue(), '"');
        } else {
          // error if not quoted at all
          error = !a.isDoubleQuoted();
        }
        if (error) {
          createViolation(element.getStartLinePosition(), "Prefer using double quotes instead of single ones.");
          break; // not more than one violation per element
        }
      }
    }
  }

}
