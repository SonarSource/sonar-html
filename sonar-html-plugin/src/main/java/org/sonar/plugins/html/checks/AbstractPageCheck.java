/*
 * SonarSource HTML analyzer :: Sonar Plugin
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
package org.sonar.plugins.html.checks;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import org.sonar.api.rule.RuleKey;
import org.sonar.plugins.html.node.Attribute;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;
import org.sonar.plugins.html.visitor.DefaultNodeVisitor;

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
    String[] tokens = value.split(",");
    for (int i = 0; i < tokens.length; i++) {
      tokens[i] = tokens[i].trim();
    }
    return tokens;
  }

  public QualifiedAttribute[] parseAttributes(String attributesList) {
    String[] qualifiedAttributeList = attributesList.split(",");

    QualifiedAttribute[] qualifiedAttributes = new QualifiedAttribute[qualifiedAttributeList.length];
    int n = 0;
    for (String qualifiedAttribute : qualifiedAttributeList) {
      qualifiedAttribute = qualifiedAttribute.trim();
      int indexOfFirstDot = qualifiedAttribute.indexOf('.');
      if (indexOfFirstDot >= 0) {
        String nodeName = qualifiedAttribute.substring(0, indexOfFirstDot);
        String attributeName = qualifiedAttribute.substring(indexOfFirstDot + 1);
        qualifiedAttributes[n] = new QualifiedAttribute(nodeName, attributeName);
      } else {
        qualifiedAttributes[n] = new QualifiedAttribute(null, qualifiedAttribute);
      }
      n++;
    }
    return qualifiedAttributes;
  }

  protected final void createViolation(Node node, String message) {
    getHtmlSourceCode().addIssue(new PreciseHtmlIssue(ruleKey, node.getStartLinePosition(), message,
      node.getStartColumnPosition(),
      node.getEndLinePosition(),
      node.getEndColumnPosition()));
  }

  protected final void createViolation(int startLine, int startColumn, int endLine, int endColumn , String message) {
    getHtmlSourceCode().addIssue(new PreciseHtmlIssue(ruleKey, startLine, message, startColumn, endLine, endColumn));
  }

  protected final void createViolation(int line, String message) {
    getHtmlSourceCode().addIssue(
      new HtmlIssue(ruleKey, line == 0 ? null : line, message)
      );
  }

  protected final void createViolation(int line, String message, Double cost) {
    getHtmlSourceCode().addIssue(
      new HtmlIssue(ruleKey, line == 0 ? null : line, message, cost)
    );
  }

  public final void setRuleKey(RuleKey ruleKey) {
    this.ruleKey = ruleKey;
  }

  protected List<Attribute> getMatchingAttributes(TagNode element, QualifiedAttribute[] attributes) {
    List<Attribute> matchingAttributes = new ArrayList<>();

    for (QualifiedAttribute qualifiedAttribute : attributes) {
      String nodeName = qualifiedAttribute.getNodeName();
      if (nodeName == null || nodeName.equalsIgnoreCase(element.getNodeName()) || nodeName.equalsIgnoreCase(element.getLocalName())) {
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
