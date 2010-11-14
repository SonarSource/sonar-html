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
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.node.ExpressionNode;
import org.sonar.plugins.web.node.TagNode;

/**
 * Checker to find scriptlets.
 *
 * @see http://java.sun.com/developer/technicalArticles/javaserverpages/code_convention/ paragraph JSP Scriptlets
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
@Rule(key = "JspScriptletCheck", name ="Scriptlets", description = "Avoid scriptlets", priority = Priority.CRITICAL,
    isoCategory = IsoCategory.Maintainability)
public class JspScriptletCheck extends AbstractPageCheck {

  private int maxLines; // TODO

  @Override
  public void expression(ExpressionNode node) {
    createViolation(node);
  }

  @Override
  public void startElement(TagNode element) {
    if (StringUtils.equalsIgnoreCase(element.getLocalName(), "scriptlet")) {
      createViolation(element);
    }
  }
}
