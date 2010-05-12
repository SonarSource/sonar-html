/*
 * Copyright (C) 2010 Matthijs Galesloot
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

package org.sonar.plugins.web.checks.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.sonar.api.batch.SensorContext;
import org.sonar.check.Check;
import org.sonar.check.IsoCategory;
import org.sonar.plugins.web.language.WebFile;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.node.NodeType;
import org.sonar.plugins.web.node.TagNode;
import org.sonar.plugins.web.rules.AbstractPageCheck;

/**
 * Checker to find unclosed tags.
 * 
 * @author Matthijs Galesloot
 */
@Check(key = "UnclosedTagCheck", description = "Tags should be properly closed", isoCategory = IsoCategory.Maintainability)
public class UnclosedTagCheck extends AbstractPageCheck {

  public List<TagNode> nodes = new ArrayList<TagNode>();

  @Override
  public void startDocument(SensorContext sensorContext, WebFile resource) {
    super.startDocument(sensorContext, resource);
    nodes.clear();
  }

  @Override
  public void startElement(TagNode element) {
    if ( !element.hasEnd()) {
      nodes.add(0, element);
    }
  }

  @Override
  public void endElement(TagNode element) {
    if (!nodes.get(0).getNodeName().equals(element.getNodeName())) {
      createViolation(nodes.get(0));
    }
    
    List<TagNode> rollup = new ArrayList<TagNode>(); 
    for (TagNode node : nodes) {
      rollup.add(node);
      if (node.getNodeName().equals(element.getNodeName())) {
        nodes.remove(rollup);
        break; 
      }
    }
  }
}