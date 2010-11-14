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
import org.sonar.check.RuleProperty;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.node.TagNode;
import org.sonar.plugins.web.node.TextNode;

/**
 * Checker to find long javascripts.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 *
 * @see http://pmd.sourceforge.net/rules/basic-jsp.html
 */
@Rule(key = "LongJavaScriptCheck", name = "Long JavaScript", description = "Avoid long JavaScript", priority = Priority.CRITICAL,
    isoCategory = IsoCategory.Maintainability)
public class LongJavaScriptCheck extends AbstractPageCheck {

  private static final int DEFAULT_MAX_LINES = 5;

  private int linesOfCode;

  @RuleProperty(key = "maxLines", description = "Max Lines")
  private int maxLines = DEFAULT_MAX_LINES;

  private TagNode scriptNode;

  @Override
  public void characters(TextNode textNode) {
    if (scriptNode != null) {
      linesOfCode += textNode.getLinesOfCode();

      if (linesOfCode > maxLines) {
        createViolation(scriptNode);
        scriptNode = null;
      }
    }
  }

  @Override
  public void endElement(TagNode element) {
    scriptNode = null;
  }

  public int getMaxLines() {
    return maxLines;
  }

  public void setMaxLines(int maxLines) {
    this.maxLines = maxLines;
  }

  @Override
  public void startElement(TagNode element) {
    if ("script".equalsIgnoreCase(element.getNodeName())) {
      scriptNode = element;
      linesOfCode = 0;
    }
  }
}
