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

package org.sonar.plugins.web.checks;

import org.apache.commons.lang.StringUtils;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.Violation;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.visitor.DefaultNodeVisitor;

/**
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
        sb.append(a.getNodeName());
        sb.append('.');
        sb.append(a.getAttributeName());
      }
    }
    return sb.toString();
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
    Violation violation = new Violation(rule);
    violation.setMessage(message);
    violation.setLineId(linePosition);
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
}
