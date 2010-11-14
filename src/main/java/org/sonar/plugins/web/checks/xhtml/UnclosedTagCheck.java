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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.sonar.check.IsoCategory;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.node.TagNode;
import org.sonar.plugins.web.visitor.WebSourceCode;

/**
 * Checker to find unclosed tags.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
@Rule(key = "UnclosedTagCheck", name ="Unclosed Tag", description = "Tags should be closed", priority = Priority.MINOR,
    isoCategory = IsoCategory.Maintainability)
public class UnclosedTagCheck extends AbstractPageCheck {

  @RuleProperty(key = "ignoreTags", description = "Ignore Tags")
  private String[] ignoreTags;

  private final List<TagNode> nodes = new ArrayList<TagNode>();

  @Override
  public void endElement(TagNode element) {
    if ( !ignoreTag(element) && nodes.size() > 0) {

      TagNode previousNode = nodes.remove(0);

      if ( !previousNode.getNodeName().equals(element.getNodeName())) {
        createViolation(previousNode);

        List<TagNode> rollup = new ArrayList<TagNode>();
        for (TagNode node : nodes) {
          rollup.add(node);
          if (node.getNodeName().equals(element.getNodeName())) {
            nodes.removeAll(rollup);
            break;
          }
        }
      }
    }
  }

  public String getIgnoreTags() {
    return StringUtils.join(ignoreTags, ",");
  }

  private boolean ignoreTag(TagNode node) {
    if (ignoreTags != null) {
      String nodeName = node.getLocalName();
      for (String ignoreTag : ignoreTags) {
        if (ignoreTag.equalsIgnoreCase(nodeName)) {
          return true;
        }
      }
    }
    return false;
  }

  public void setIgnoreTags(String value) {
    ignoreTags = StringUtils.split(value, ',');
  }

  @Override
  public void startDocument(WebSourceCode webSourceCode) {
    super.startDocument(webSourceCode);
    nodes.clear();
  }

  @Override
  public void startElement(TagNode element) {
    if ( !ignoreTag(element)) {
      nodes.add(0, element);
    }
  }

  @Override
  public void endDocument() {
    for (TagNode node : nodes) {
      createViolation(node);
    }
  }
}