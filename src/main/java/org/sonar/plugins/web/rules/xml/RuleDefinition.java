/*
 * Copyright (C) 2010
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

package org.sonar.plugins.web.rules.xml;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("rule")
public class RuleDefinition implements Comparable<String> {

  @XStreamAlias("class")
  @XStreamAsAttribute
  private String clazz;

  @XStreamAsAttribute
  private String message;

  @XStreamAsAttribute
  private String name;

  private String priority;

  private List<Property> properties;

  @XStreamOmitField
  private String description;

  @XStreamOmitField
  private String category;

  @XStreamOmitField
  private String exclude;

  @XStreamOmitField
  private String example;

  public RuleDefinition(String clazz) {
    this(clazz, null);
  }

  public RuleDefinition(String clazz, String priority) {
    this.clazz = clazz;
    this.priority = priority;
  }

  public void addProperty(Property property) {
    if (properties == null) {
      properties = new ArrayList<Property>();
    }
    properties.add(property);
  }

  public int compareTo(String o) {
    return o.compareTo(clazz);
  }

  public String getCategory() {
    return category;
  }

  public String getClazz() {
    return clazz;
  }

  public String getDescription() {
    String desc = "";
    if (description != null) {
      desc += "<p>" + description + "</p>";
    }
    if (example != null) {
      desc += "<pre>" + example + "</pre>";
    }
    return desc;
  }

  public String getMessage() {
    return message;
  }

  public String getName() {
    return name;
  }

  public String getPriority() {
    return priority;
  }

  public List<Property> getProperties() {
    return properties;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPriority(String priority) {
    this.priority = priority;
  }

  public void setProperties(List<Property> properties) {
    this.properties = properties;
  }
}