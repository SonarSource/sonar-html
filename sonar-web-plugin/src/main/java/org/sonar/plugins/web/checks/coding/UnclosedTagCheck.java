/*
 * SonarWeb :: SonarQube Plugin
 * Copyright (c) 2010-2018 SonarSource SA and Matthijs Galesloot
 * sonarqube@googlegroups.com
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
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.node.TagNode;

import java.util.ArrayList;
import java.util.List;

@Rule(key = "UnclosedTagCheck")
public class UnclosedTagCheck extends AbstractPageCheck {

  private static final String DEFAULT_IGNORE_TAGS = "HTML,HEAD,BODY,P,DT,DD,LI,OPTION,THEAD,TH,TBODY,TR,TD,TFOOT,COLGROUP" +
    ",IMG,INPUT,BR,HR,FRAME,AREA,BASE,BASEFONT,COL,ISINDEX,LINK,META,PARAM";

  @RuleProperty(
    key = "ignoreTags",
    description = "Ignore tags",
    defaultValue = DEFAULT_IGNORE_TAGS)
  public String ignoreTags = DEFAULT_IGNORE_TAGS;

  private String[] ignoreTagsArray;
  private final List<TagNode> nodes = new ArrayList<>();

  @Override
  public void startDocument(List<Node> nodes) {
    ignoreTagsArray = StringUtils.split(ignoreTags, ',');
    this.nodes.clear();
  }

  @Override
  public void endElement(TagNode element) {
    if (!ignoreTag(element) && !nodes.isEmpty()) {

      TagNode previousNode = nodes.remove(0);

      if (!previousNode.getNodeName().equals(element.getNodeName())) {
        createViolation(previousNode.getStartLinePosition(), "The tag \"" + previousNode.getNodeName() + "\" has no corresponding closing tag.");

        List<TagNode> rollup = new ArrayList<>();
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

  private boolean ignoreTag(TagNode node) {
    for (String ignoreTag : ignoreTagsArray) {
      if (node.equalsElementName(ignoreTag)) {
        return true;
      }
    }
    return false;
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
      createViolation(node.getStartLinePosition(), "The tag \"" + node.getNodeName() + "\" has no corresponding closing tag.");
    }
  }

}
