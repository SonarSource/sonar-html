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

import org.apache.commons.lang.StringUtils;
import org.sonar.check.IsoCategory;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.checks.Utils;
import org.sonar.plugins.web.node.Attribute;
import org.sonar.plugins.web.node.DirectiveNode;

/**
 * Checker to find disallowed taglibs.
 *
 * e.g. <%@ taglib prefix="sql" uri="http://java.sun.com/jstl/sql" %>
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
@Rule(key = "IllegalTagLibsCheck", name = "Illegal TagLibs", description = "Certain taglibs are not allowed",
    priority = Priority.CRITICAL, isoCategory = IsoCategory.Maintainability)
public class IllegalTagLibsCheck extends AbstractPageCheck {

  @RuleProperty(key = "tagLibs", description = "Disallowed Taglibs")
  private String[] tagLibs = new String[] { "http://java.sun.com/jstl/sql" };

  @Override
  public void directive(DirectiveNode node) {
    if ("taglib".equalsIgnoreCase(node.getNodeName())) {
      for (Attribute a : node.getAttributes()) {
        for (String tagLib : tagLibs) {
          if (tagLib.equalsIgnoreCase(a.getValue())) {
            createViolation(node);
          }
        }
      }
    }
  }

  public String getIgnoreTags() {
    return StringUtils.join(tagLibs, ",");
  }

  public void setIgnoreTags(String value) {
    tagLibs = Utils.trimSplitCommaSeparatedList(value);
  }
}