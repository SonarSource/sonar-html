/*
 * Sonar Web Plugin
 * Copyright (C) 2010 Matthijs Galesloot and SonarSource
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
package org.sonar.plugins.web.checks.coding;

import org.apache.commons.lang.StringUtils;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.node.TagNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Checker to find unclosed tags.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
@Rule(key = "UnclosedTagCheck", priority = Priority.MINOR)
public class UnclosedTagCheck extends AbstractPageCheck {

  @RuleProperty
  private String[] ignoreTags;

  private final List<TagNode> nodes = new ArrayList<TagNode>();

  @Override
  public void endElement(TagNode element) {
    if (!ignoreTag(element) && !nodes.isEmpty()) {

      TagNode previousNode = nodes.remove(0);

      if (!previousNode.getNodeName().equals(element.getNodeName())) {
        createViolation(previousNode.getStartLinePosition(), "The tag '" + previousNode.getNodeName() + "' has no corresponding closing tag.");

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
      for (String ignoreTag : ignoreTags) {
        if (node.equalsElementName(ignoreTag)) {
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
  public void startDocument(List<Node> nodes) {
    this.nodes.clear();
  }

  @Override
  public void startElement(TagNode element) {
    if (!ignoreTag(element)) {
      nodes.add(0, element);
    }
  }

  @Override
  public void endDocument() {
    for (TagNode node : nodes) {
      createViolation(node.getStartLinePosition(), "This tag has no corresponding closing tag.");
    }
  }

}
