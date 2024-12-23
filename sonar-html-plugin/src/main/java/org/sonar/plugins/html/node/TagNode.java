/*
 * SonarQube HTML Plugin :: Sonar Plugin
 * Copyright (C) 2010-2024 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package org.sonar.plugins.html.node;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

/**
 * Defines a tag.
 *

 */
public class TagNode extends Node {

  private final List<Attribute> attributes = new ArrayList<>();
  private final List<TagNode> children = new ArrayList<>();
  private String nodeName;
  private TagNode parent;

  public TagNode() {
    super(NodeType.TAG);
  }

  protected TagNode(NodeType nodeType) {
    super(nodeType);
  }

  public boolean equalsElementName(String elementName) {
    return getLocalName().equalsIgnoreCase(elementName) || getNodeName().equalsIgnoreCase(elementName);
  }

  /**
   *  This method takes into account the property binding mechanism of angular and vue.js. See SONARHTML-92, SONARHTML-113, SONARHTML-118, SONARHTML-158
   */
  @Nullable
  public Attribute getProperty(String propertyName) {
    String angularProperty = "[" + propertyName + "]";
    String angularAttrProperty = "[attr." + propertyName + "]";
    String shortAngularAttrProperty = "attr." + propertyName;
    String vueProperty = "v-bind:" + propertyName;
    String vueShorthandProperty = ":" + propertyName;
    String vueSquaredShorthandProperty = ":[" + propertyName + "]";
    for (Attribute a : attributes) {
      String attributeName = a.getName();
      if (propertyName.equalsIgnoreCase(attributeName)
          || angularProperty.equalsIgnoreCase(attributeName) || angularAttrProperty.equalsIgnoreCase(attributeName) || shortAngularAttrProperty.equalsIgnoreCase(attributeName)
          || vueProperty.equalsIgnoreCase(attributeName) || vueShorthandProperty.equalsIgnoreCase(attributeName)
          || vueSquaredShorthandProperty.equalsIgnoreCase(attributeName)) {
        return a;
      }
    }
    return null;
  }

  @Nullable
  public String getPropertyValue(String propertyName) {
    Attribute property = getProperty(propertyName);
    if (property != null) {
      return property.getValue();
    }
    return null;
  }

  public boolean hasProperty(String propertyName) {
    return getProperty(propertyName) != null;
  }

  @CheckForNull
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

  public boolean hasAttribute(String attributeName) {
    return getAttribute(attributeName) != null;
  }

  public List<TagNode> getChildren() {
    return children;
  }

  public String getLocalName() {
    String[] parts = getNodeName().split(":");
    String localPart = parts[parts.length - 1];
    if (localPart.isEmpty()) {
      return getNodeName();
    } else {
      return localPart;
    }
  }

  public String getNodeName() {
    return nodeName == null ? "" : nodeName;
  }

  @Nullable
  public TagNode getParent() {
    return parent;
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

  public void setParent(@Nullable TagNode parent) {
    this.parent = parent;
    if (parent != null) {
      parent.getChildren().add(this);
    }
  }
}
