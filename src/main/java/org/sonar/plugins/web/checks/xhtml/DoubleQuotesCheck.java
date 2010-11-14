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

import org.apache.commons.lang.StringUtils;
import org.sonar.check.IsoCategory;
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
@Rule(key = "DoubleQuotesCheck", name = "Double Quotes", description = "Use double quotes for attribute values",
    priority = Priority.MINOR, isoCategory = IsoCategory.Maintainability)
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
          createViolation(element);
          break; // not more than one violation per element
        }
      }
    }
  }

}