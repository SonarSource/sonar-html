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

package org.sonar.plugins.web.node;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * @author Matthijs Galesloot
 */
public class TagNode extends Node {

  private final List<Attribute> attributes = new ArrayList<Attribute>();
  private String nodeName;

  public TagNode() {
    super(NodeType.Tag);
  }

  protected TagNode(NodeType nodeType) {
    super(nodeType);
  }

  public String getAttribute(String attributeName) {

    for (Attribute a : attributes) {
      if (attributeName.equalsIgnoreCase(a.getName())) {
        return a.getValue();
      }
    }
    return null;
  }

  public List<Attribute> getAttributes() {
    return attributes;
  }

  public String getNodeName() {
    return nodeName;
  }

  public String getLocalName() {
    String localPart = StringUtils.substringAfterLast(getNodeName(), ":");
    if (localPart == null) {
      return nodeName;
    } else {
      return localPart;
    }
  }

  public boolean hasEnd() {
    return getCode().endsWith("/>");
  }

  public boolean isEndElement() {
    return getCode().startsWith("</");
  }

  public void setNodeName(String nodeName) {
    this.nodeName = nodeName;
  }

  public boolean equalsElementName(String elementName) {
    return StringUtils.equalsIgnoreCase(getLocalName(), elementName) || StringUtils.equalsIgnoreCase(getNodeName(), elementName);
  }
}