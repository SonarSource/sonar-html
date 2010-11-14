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
import org.sonar.check.RuleProperty;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.checks.Utils;
import org.sonar.plugins.web.node.TagNode;

/**
 * Checker for occurrence of disallowed elements.
 *
 * e.g. element <applet> should not be used.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
@Rule(key = "IllegalElementCheck", name ="IllegalElement", description = "element should not be used", priority = Priority.MAJOR,
    isoCategory = IsoCategory.Reliability)
public class IllegalElementCheck extends AbstractPageCheck {

  @RuleProperty(key = "elements", description = "elements")
  private String[] elements;

  public String getElements() {
    if (elements != null) {
      return StringUtils.join(elements, ",");
    }
    return "";
  }

  public void setElements(String elementList) {
    elements = Utils.trimSplitCommaSeparatedList(elementList);
  }

  @Override
  public void startElement(TagNode element) {

    if (elements == null) {
      return;
    }

    for (String elementName : elements) {
      if (StringUtils.equalsIgnoreCase(element.getLocalName(), elementName)
          || StringUtils.equalsIgnoreCase(element.getNodeName(), elementName)) {
        createViolation(element.getStartLinePosition(), getRule().getDescription() + ": " + elementName);
      }
    }
  }
}
