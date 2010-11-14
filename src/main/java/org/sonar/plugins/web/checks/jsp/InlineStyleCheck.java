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
 * Checker for occurrence of inline style.
 *
 * @see http://java.sun.com/developer/technicalArticles/javaserverpages/code_convention/ paragraph Cascading Style Sheets
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
@Rule(key = "InlineStyleCheck", name = "Inline Style", description = "Inline style should be avoided", priority = Priority.MINOR,
    isoCategory = IsoCategory.Maintainability)
public class InlineStyleCheck extends AbstractPageCheck {

  @Override
  public void startElement(TagNode element) {

    if ("style".equalsIgnoreCase(element.getLocalName())) {
      createViolation(element);
    }
  }
}