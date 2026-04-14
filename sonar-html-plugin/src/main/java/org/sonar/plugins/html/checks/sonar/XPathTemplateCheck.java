/*
 * SonarQube HTML
 * Copyright (C) SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * You can redistribute and/or modify this program under the terms of
 * the Sonar Source-Available License Version 1, as published by SonarSource Sàrl.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package org.sonar.plugins.html.checks.sonar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Attribute;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.NodeType;
import org.sonar.plugins.html.node.TagNode;
import org.sonar.plugins.html.node.TextNode;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Template rule to check HTML elements using XPath expressions.
 * This rule allows users to define custom XPath expressions to find HTML elements
 * and report issues when matches are found.
 * 
 * This implementation follows the same principles as the XML plugin's
 * XPathCheck (RSPEC-140), but adapted for HTML documents.
 * 
 * Uses standard javax.xml.xpath API for proper XPath evaluation by converting
 * the HTML plugin's DOM structure to W3C DOM.
 */
@Rule(key = "S140")
public class XPathTemplateCheck extends AbstractPageCheck {

  private static final Logger LOG = LoggerFactory.getLogger(XPathTemplateCheck.class);
  private static final String DEFAULT_XPATH_EXPRESSION = "";
  private static final String DEFAULT_MESSAGE = "The XPath expression matches this piece of code";

  @RuleProperty(
    key = "expression",
    description = "The XPath query",
    defaultValue = DEFAULT_XPATH_EXPRESSION,
    type = "TEXT")
  public String expression = DEFAULT_XPATH_EXPRESSION;

  @RuleProperty(
    key = "filePattern",
    description = "The files to be validated using Ant-style matching patterns")
  public String filePattern;

  @RuleProperty(
    key = "message",
    description = "The issue message",
    defaultValue = DEFAULT_MESSAGE)
  public String message = DEFAULT_MESSAGE;

  private XPathExpression compiledExpression;
  private Document w3cDocument;
  private Map<org.w3c.dom.Node, TagNode> nodeMapping;

  @Override
  public void startDocument(List<Node> nodes) {
    // Reset state to prevent stale values from previous files
    this.nodeMapping = new HashMap<>();
    this.compiledExpression = null;
    this.w3cDocument = null;
    
    if (!isFileIncluded() || expression == null || expression.trim().isEmpty()) {
      return;
    }

    try {
      // Compile XPath expression
      XPath xpath = XPathFactory.newInstance().newXPath();
      compiledExpression = xpath.compile(expression.trim());

      // Build W3C DOM from HTML plugin's node structure
      w3cDocument = buildW3cDocument(nodes);
    } catch (XPathExpressionException e) {
      LOG.error("Failed to compile XPath expression [{}]: {}", expression, e.getMessage());
    } catch (ParserConfigurationException | DOMException e) {
      LOG.debug("Failed to build DOM for XPath evaluation: {}", e.getMessage());
      compiledExpression = null;
      w3cDocument = null;
    }
  }

  @Override
  public void endDocument() {
    if (compiledExpression == null || w3cDocument == null) {
      return;
    }

    try {
      // Evaluate XPath expression - try as NODESET first
      NodeList matchedNodes = (NodeList) compiledExpression.evaluate(w3cDocument, XPathConstants.NODESET);
      
      for (int i = 0; i < matchedNodes.getLength(); i++) {
        org.w3c.dom.Node w3cNode = matchedNodes.item(i);
        TagNode htmlNode = nodeMapping.get(w3cNode);
        if (htmlNode != null) {
          createViolation(htmlNode.getStartLinePosition(), getMessage());
        } else {
          LOG.debug("Could not find matching HTML node for W3C node: {}", w3cNode.getNodeName());
        }
      }
    } catch (XPathExpressionException nodeSetException) {
      // If NODESET evaluation fails, try as BOOLEAN for file-level issues
      try {
        Boolean result = (Boolean) compiledExpression.evaluate(w3cDocument, XPathConstants.BOOLEAN);
        if (Boolean.TRUE.equals(result)) {
          createViolation(0, getMessage());
        }
      } catch (XPathExpressionException booleanException) {
        LOG.debug("XPath evaluation failed: {}", booleanException.getMessage());
      }
    }
  }

  private String getMessage() {
    if (message != null && !message.trim().isEmpty()) {
      return message;
    }
    return "Change this HTML node to not match: " + expression;
  }

  private boolean isFileIncluded() {
    if (filePattern == null) {
      return true;
    }
    String filePath = getHtmlSourceCode().inputFile().uri().getPath();
    return org.sonar.api.utils.WildcardPattern.create(filePattern).match(filePath);
  }

  private Document buildW3cDocument(List<Node> nodes) throws ParserConfigurationException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(false);
    
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document doc = builder.newDocument();
    
    // Create root element to hold all nodes
    Element root = doc.createElement("root");
    doc.appendChild(root);
    
    // Build mapping of TagNode to W3C Element for text node attachment
    Map<TagNode, Element> tagToElementMap = new HashMap<>();
    
    // Convert HTML nodes to W3C DOM in a single pass
    // The nodes list contains ALL nodes in document order (TagNode, TextNode, etc.)
    for (Node node : nodes) {
      if (node instanceof TagNode tagNode) {
        if (isRootStartElement(tagNode)) {
          convertToW3cNode(doc, root, tagNode, tagToElementMap);
        }
      } else if (node instanceof TextNode textNode) {
        convertTextNode(doc, textNode, tagToElementMap);
      }
    }
    
    return doc;
  }

  /**
   * Checks if a TagNode is a root-level start element that should be converted to W3C DOM.
   * Excludes end elements (e.g. &lt;/div&gt;) and directives (e.g. &lt;!DOCTYPE&gt;).
   */
  private static boolean isRootStartElement(TagNode tagNode) {
    return tagNode.getParent() == null
      && !tagNode.isEndElement()
      && tagNode.getNodeType() != NodeType.DIRECTIVE;
  }

  private void convertToW3cNode(Document doc, org.w3c.dom.Node parent, TagNode htmlNode, Map<TagNode, Element> tagToElementMap) {
    Element element = doc.createElement(htmlNode.getNodeName());
    
    // Add attributes
    for (Attribute attr : htmlNode.getAttributes()) {
      element.setAttribute(attr.getName(), attr.getValue());
    }
    
    // Store mapping for line number lookup
    nodeMapping.put(element, htmlNode);
    
    // Store mapping for text node attachment
    tagToElementMap.put(htmlNode, element);
    
    parent.appendChild(element);
    
    // Process children
    for (TagNode child : htmlNode.getChildren()) {
      convertToW3cNode(doc, element, child, tagToElementMap);
    }
  }

  private static void convertTextNode(Document doc, TextNode textNode, Map<TagNode, Element> tagToElementMap) {
    // Skip blank text nodes (whitespace-only)
    if (textNode.isBlank()) {
      return;
    }
    
    // Find parent element
    TagNode parent = textNode.getParent();
    if (parent != null) {
      Element parentElement = tagToElementMap.get(parent);
      if (parentElement != null) {
        org.w3c.dom.Text w3cText = doc.createTextNode(textNode.getCode());
        parentElement.appendChild(w3cText);
      }
    }
  }
}
