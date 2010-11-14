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