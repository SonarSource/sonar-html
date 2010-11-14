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


import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.sonar.check.IsoCategory;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.checks.Utils;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.node.NodeType;
import org.sonar.plugins.web.node.TagNode;
import org.sonar.plugins.web.visitor.WebSourceCode;

/**
 * Checker for occurrence of required elements, e.g. a security tag.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
@Rule(key = "RequiredElementCheck", name ="RequiredElement", description = "Required element must be used", priority = Priority.MAJOR,
    isoCategory = IsoCategory.Reliability)
public class RequiredElementCheck extends AbstractPageCheck {

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
  public void startDocument(WebSourceCode webSourceCode, List<Node> nodes) {
    super.startDocument(webSourceCode, nodes);

    if (elements == null) {
      return;
    }

    for (String elementName : elements) {
      boolean hasRequiredElement = false;
      for (Node node : nodes) {
        if (node.getNodeType() == NodeType.Tag) {
          TagNode element = (TagNode) node;
          if (element.equalsElementName(elementName)) {
            hasRequiredElement = true;
            break;
          }
        }
      }
      if ( !hasRequiredElement) {
        createViolation(0, getRule().getDescription() + ": " + elementName);
      }
    }
  }
}
