/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (c) 2010-2022 SonarSource SA and Matthijs Galesloot
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
package org.sonar.plugins.html.checks.coding;

import java.util.Arrays;
import org.apache.commons.lang.StringUtils;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;

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

  private List<String> ignoreTagsList;
  private final List<TagNode> nodes = new ArrayList<>();

  private boolean skipFile = false;

  @Override
  public void startDocument(List<Node> nodes) {
    skipFile = false;
    if (isCshtmlFile()) {
      // This rule is performing poorly in presence of Razor syntax (https://docs.microsoft.com/en-us/aspnet/core/mvc/views/razor?view=aspnetcore-3.1)
      // present in cshtml files, we skip this file for this rule.
      skipFile = true;
      return;
    }

    if(ignoreTagsList == null) {
      ignoreTagsList = Arrays.asList(StringUtils.split(ignoreTags, ','));
    }
    this.nodes.clear();
  }

  @Override
  public void endElement(TagNode element) {
    if (skipFile) {
      return;
    }
    if (isNotIgnoreTag(element) && !nodes.isEmpty()) {
      TagNode previousNode = nodes.remove(0);
      if (!previousNode.getNodeName().equals(element.getNodeName())) {
        createViolation(previousNode, "The tag \"" + previousNode.getNodeName() + "\" has no corresponding closing tag.");
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

  private boolean isNotIgnoreTag(TagNode node) {
    String nodeName = node.getNodeName();
    return !(nodeName != null && nodeName.startsWith("!")) && ignoreTagsList.stream().noneMatch(node::equalsElementName);
  }

  @Override
  public void startElement(TagNode element) {
    if (skipFile) {
      return;
    }
    if (isNotIgnoreTag(element)) {
      nodes.add(0, element);
    }
  }

  @Override
  public void endDocument() {
    if (skipFile) {
      return;
    }
    for (TagNode node : nodes) {
      createViolation(node, "The tag \"" + node.getNodeName() + "\" has no corresponding closing tag.");
    }
  }

  private boolean isCshtmlFile() {
    return getHtmlSourceCode().inputFile().filename().endsWith(".cshtml");
  }

}
