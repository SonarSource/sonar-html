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

import org.sonar.check.IsoCategory;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.node.TagNode;

/**
 * Checker to find dynamic includes.
 *
 * e.g. <jsp:include page="header.jsp">
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
@Rule(key = "DynamicJspIncludeCheck", name = "Dynamic Jsp Include", description = "Dynamic Jsp Include is not allowed",
    priority = Priority.CRITICAL, isoCategory = IsoCategory.Maintainability)
public class DynamicJspIncludeCheck extends AbstractPageCheck {

  @Override
  public void startElement(TagNode node) {
    if ("jsp:include".equals(node.getNodeName())) {
      createViolation(node);
    }
  }
}