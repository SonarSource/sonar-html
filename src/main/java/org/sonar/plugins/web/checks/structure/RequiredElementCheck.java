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

package org.sonar.plugins.web.checks.structure;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.web.checks.AbstractPageCheck;
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
@Rule(key = "RequiredElementCheck", name = "Element Required", description = "Required element must be used", priority = Priority.MAJOR)
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
    elements = trimSplitCommaSeparatedList(elementList);
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
