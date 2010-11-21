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
@Rule(key = "UnclosedTagCheck", name = "Unclosed Tag", description = "Tags should be closed", priority = Priority.MINOR,
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