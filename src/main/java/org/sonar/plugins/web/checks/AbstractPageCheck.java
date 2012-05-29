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

package org.sonar.plugins.web.checks;

import org.apache.commons.lang.StringUtils;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.Violation;
import org.sonar.plugins.web.node.Attribute;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.node.TagNode;
import org.sonar.plugins.web.visitor.DefaultNodeVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Abtract superclass for checks.
 *
 * @author Matthijs Galesloot
 */
public abstract class AbstractPageCheck extends DefaultNodeVisitor {

  private Rule rule;

  protected static final class QualifiedAttribute {

    private String attributeName;

    private String nodeName;

    private QualifiedAttribute(String nodeName, String attributeName) {
      this.setNodeName(nodeName);
      this.setAttributeName(attributeName);
    }

    public void setAttributeName(String attributeName) {
      this.attributeName = attributeName;
    }

    public String getAttributeName() {
      return attributeName;
    }

    public void setNodeName(String nodeName) {
      this.nodeName = nodeName;
    }

    public String getNodeName() {
      return nodeName;
    }
  }

  protected String getAttributesAsString(QualifiedAttribute[] qualifiedAttributes) {
    StringBuilder sb = new StringBuilder();
    if (qualifiedAttributes != null) {
      for (QualifiedAttribute a : qualifiedAttributes) {
        if (sb.length() > 0) {
          sb.append(",");
        }
        if (a.getNodeName() != null) {
          sb.append(a.getNodeName());
          sb.append('.');
        }
        sb.append(a.getAttributeName());
      }
    }
    return sb.toString();
  }

  public boolean isUnifiedExpression(String value) {
    return value != null && value.length() > 0 && (value.contains("#{") || value.contains("${"));
  }

  public String[] trimSplitCommaSeparatedList(String value) {
    String[] tokens = StringUtils.split(value, ",");
    for (int i = 0; i < tokens.length; i++) {
      tokens[i] = tokens[i].trim();
    }
    return tokens;
  }

  public QualifiedAttribute[] parseAttributes(String attributesList) {
    String[] qualifiedAttributeList = StringUtils.split(attributesList, ",");

    QualifiedAttribute[] qualifiedAttributes = new QualifiedAttribute[qualifiedAttributeList.length];
    int n = 0;
    for (String qualifiedAttribute : qualifiedAttributeList) {
      qualifiedAttribute = qualifiedAttribute.trim();
      if (qualifiedAttribute.indexOf('.') >= 0) {
        qualifiedAttributes[n++] = new QualifiedAttribute(StringUtils.substringBefore(qualifiedAttribute, "."), StringUtils.substringAfter(
            qualifiedAttribute, "."));
      } else {
        qualifiedAttributes[n++] = new QualifiedAttribute(null, qualifiedAttribute);
      }
    }
    return qualifiedAttributes;
  }

  protected final void createViolation(int linePosition) {
    createViolation(linePosition, rule.getDescription());
  }

  protected final void createViolation(int linePosition, String message) {
    Violation violation = Violation.create(rule, getWebSourceCode().getResource());
    violation.setMessage(message);
    violation.setLineId(linePosition == 0 ? null : linePosition);
    getWebSourceCode().addViolation(violation);
  }

  protected final void createViolation(Node node) {
    createViolation(node.getStartLinePosition());
  }

  public final Rule getRule() {
    return rule;
  }

  public final String getRuleKey() {
    return rule.getConfigKey();
  }

  public final void setRule(Rule rule) {
    this.rule = rule;
  }

  protected List<Attribute> getMatchingAttributes(TagNode element, QualifiedAttribute[] attributes) {
    List<Attribute> matchingAttributes = new ArrayList<Attribute>();

    for (QualifiedAttribute qualifiedAttribute : attributes) {
      if (qualifiedAttribute.getNodeName() == null
        || StringUtils.equalsIgnoreCase(element.getLocalName(), qualifiedAttribute.getNodeName())
        || StringUtils.equalsIgnoreCase(element.getNodeName(), qualifiedAttribute.getNodeName())) {
        for (Attribute a : element.getAttributes()) {
          if (qualifiedAttribute.getAttributeName().equalsIgnoreCase(a.getName())) {
            matchingAttributes.add(a);
          }
        }
      }
    }
    return matchingAttributes;
  }
}
