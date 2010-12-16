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

package org.sonar.plugins.web.checks.generic;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.sonar.api.utils.SonarException;
import org.sonar.check.IsoCategory;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.visitor.WebSourceCode;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

@Rule(key = "XPathCheck", name = "XPath Check", description = "XPath Check", priority = Priority.CRITICAL,
    isoCategory = IsoCategory.Reliability)
public class XPathCheck extends AbstractPageCheck {

  private final class XPathNamespaceContext implements NamespaceContext {

    private final Map<String, String> namespaceMap = new HashMap<String, String>();

    public XPathNamespaceContext() {

      for (String namespace : namespaces) {
        String[] nn = StringUtils.split(namespace, "=");
        if (nn.length > 1) {
          // unquote the namespace
          if (nn[1].startsWith("\"")){
            nn[1] = StringUtils.strip(nn[1], "\"");
          }
          namespaceMap.put(nn[0], nn[1]);
        }
      }
    }

    public String getNamespaceURI(String prefix) {
      return namespaceMap.get(prefix);
    }

    // Dummy implemenation - not used!
    public String getPrefix(String uri) {
      return null;
    }

    // Dummy implementation - not used!
    public Iterator getPrefixes(String val) {
      return null;
    }
  }

  @RuleProperty(key = "expression")
  private String expression;

  @RuleProperty(key = "namespaces", description = "Namespaces")
  private String[] namespaces;

  private XPathExpression xPathExpression;

  private void evaluateXPath() {

    InputStream inputStream = getWebSourceCode().getInputStream();
    Document document = new DocumentBuilder().createDomDocument(inputStream, namespaces != null);

    try {
      NodeList nodes = (NodeList) getXPathExpression().evaluate(document, XPathConstants.NODESET);
      for (int i = 0; i < nodes.getLength(); i++) {

        int lineNumber = DocumentBuilder.getLineNumber(nodes.item(i));
        createViolation(lineNumber);
      }
    } catch (XPathExpressionException e) {
      throw new SonarException(e);
    }
  }

  public String getExpression() {
    return expression;
  }

  public String getNamespaces() {
    if (namespaces != null) {
      return StringUtils.join(namespaces, ",");
    }
    return "";
  }

  private XPathExpression getXPathExpression() {
    if (expression != null && xPathExpression == null) {
      try {
        XPath xpath = XPathFactory.newInstance().newXPath();
        if (!ArrayUtils.isEmpty(namespaces)) {
          xpath.setNamespaceContext(new XPathNamespaceContext());
        }
        xPathExpression = xpath.compile(expression);
      } catch (XPathExpressionException e) {
        throw new SonarException(e);
      }
    }

    return xPathExpression;
  }

  public void setExpression(String expression) {
    this.expression = expression;
  }

  public void setNamespaces(String list) {
    namespaces = StringUtils.split(list, ",");
  }

  @Override
  public void startDocument(WebSourceCode webSourceCode) {
    super.startDocument(webSourceCode);

    if (getXPathExpression() != null) {
      evaluateXPath();
    }
  }
}
