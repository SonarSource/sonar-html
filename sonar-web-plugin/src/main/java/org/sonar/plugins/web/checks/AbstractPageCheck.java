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
package org.sonar.plugins.web.checks;

import org.apache.commons.lang.StringUtils;
import org.sonar.api.rule.RuleKey;
import org.sonar.plugins.web.node.Attribute;
import org.sonar.plugins.web.node.TagNode;
import org.sonar.plugins.web.visitor.DefaultNodeVisitor;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Abtract superclass for checks.
 *
 * @author Matthijs Galesloot
 */
public abstract class AbstractPageCheck extends DefaultNodeVisitor {

  private RuleKey ruleKey;

  protected static final class QualifiedAttribute {

    private String attributeName;

    private String nodeName;

    private QualifiedAttribute(@Nullable String nodeName, String attributeName) {
      this.setNodeName(nodeName);
      this.setAttributeName(attributeName);
    }

    public void setAttributeName(String attributeName) {
      this.attributeName = attributeName;
    }

    public String getAttributeName() {
      return attributeName;
    }

    public void setNodeName(@Nullable String nodeName) {
      this.nodeName = nodeName;
    }

    @Nullable
    public String getNodeName() {
      return nodeName;
    }

  }

  public boolean isUnifiedExpression(@Nullable String value) {
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
        qualifiedAttributes[n] = new QualifiedAttribute(StringUtils.substringBefore(qualifiedAttribute, "."), StringUtils.substringAfter(
          qualifiedAttribute, "."));
      } else {
        qualifiedAttributes[n] = new QualifiedAttribute(null, qualifiedAttribute);
      }
      n++;
    }
    return qualifiedAttributes;
  }

  protected final void createViolation(int line, String message) {
    getWebSourceCode().addIssue(
      new WebIssue(ruleKey, line == 0 ? null : line, message)
      );
  }

  protected final void createViolation(int line, String message, Double cost) {
    getWebSourceCode().addIssue(
      new WebIssue(ruleKey, line == 0 ? null : line, message, cost)
    );
  }

  public final void setRuleKey(RuleKey ruleKey) {
    this.ruleKey = ruleKey;
  }

  protected List<Attribute> getMatchingAttributes(TagNode element, QualifiedAttribute[] attributes) {
    List<Attribute> matchingAttributes = new ArrayList<>();

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
