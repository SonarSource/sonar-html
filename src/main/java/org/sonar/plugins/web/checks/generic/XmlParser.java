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

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.sonar.api.utils.SonarException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Parse XML files
 *
 * @author Matthijs Galesloot
 *
 */
public final class XmlParser {

  private static final SAXParserFactory SAX_FACTORY;

  /**
   * Build the SAXParserFactory.
   */
  static {

    SAX_FACTORY = SAXParserFactory.newInstance();

    try {
      SAX_FACTORY.setNamespaceAware(true);
      SAX_FACTORY.setValidating(false);
      SAX_FACTORY.setFeature("http://xml.org/sax/features/validation", false);
      SAX_FACTORY.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
      SAX_FACTORY.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
      SAX_FACTORY.setFeature("http://xml.org/sax/features/external-general-entities", false);
      SAX_FACTORY.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
    } catch (SAXException e) {
      throw new SonarException(e);
    } catch (ParserConfigurationException e) {
      throw new SonarException(e);
    }
  }

  public Document createDomDocument(File file) {

    try {
      SAXParser parser = SAX_FACTORY.newSAXParser();

      Document document = DocumentBuilderFactory.
        newInstance().
        newDocumentBuilder().
        newDocument();

      LocationRecordingHandler handler = new LocationRecordingHandler(document);
      parser.parse(file, handler);

      return document;
    } catch (ParserConfigurationException e) {
      throw new SonarException(e);
    } catch (SAXException e) {
      throw new SonarException(e);
    } catch (IOException e) {
      throw new SonarException(e);
    }
  }

  /**
   * From http://will.thestranathans.com/post/1026712315/getting-line-numbers-from-xpath-in-java
   *
   */
  class LocationRecordingHandler extends DefaultHandler {
    public static final String KEY_LINE_NO = "com.will.LineNumber";
    public static final String KEY_COLUMN_NO = "com.will.ColumnNumber";

    private final Document doc;
    private Locator locator = null;
    private Element current;

    // The docs say that parsers are "highly encouraged" to set this
    public LocationRecordingHandler(Document doc) {
      this.doc = doc;
    }

    @Override
    public void setDocumentLocator(Locator locator) {
      this.locator = locator;
    }

    // This just takes the location info from the locator and puts
    // it into the provided node
    private void setLocationData(Node n) {
      if (locator != null) {
        n.setUserData(KEY_LINE_NO, locator.getLineNumber(), null);
        n.setUserData(KEY_COLUMN_NO, locator.getColumnNumber(), null);
      }
    }

    // Admittedly, this is largely lifted from other examples
    @Override
    public void startElement(
      String uri, String localName, String qName, Attributes attrs) {
      Element e = null;
      if (localName != null && !"".equals(localName)) {
        e = doc.createElementNS(uri, localName);
      } else {
        e = doc.createElement(qName);
      }

      // But this part isn't lifted ;)
      setLocationData(e);

      if (current == null) {
        doc.appendChild(e);
      } else {
        current.appendChild(e);
      }
      current = e;

      // For each attribute, make a new attribute in the DOM, append it
      // to the current element, and set the column and line numbers.
      if (attrs != null) {
        for (int i = 0; i < attrs.getLength(); i++) {
          Attr attr = null;
          if (attrs.getLocalName(i) != null && !"".equals(attrs.getLocalName(i))) {
            attr = doc.createAttributeNS(attrs.getURI(i), attrs.getLocalName(i));
            attr.setValue(attrs.getValue(i));
            setLocationData(attr);
            current.setAttributeNodeNS(attr);
          } else {
            attr = doc.createAttribute(attrs.getQName(i));
            attr.setValue(attrs.getValue(i));
            setLocationData(attr);
            current.setAttributeNode(attr);
          }
        }
      }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
      Node parent;

      if (current == null) {
        return;
      }

      parent = current.getParentNode();
      // If the parent is the document itself, then we're done.
      if (parent.getParentNode() == null) {
        current.normalize();
        current = null;
      } else {
        current = (Element)current.getParentNode();
      }
    }

    // Even with text nodes, we can record the line and column number
    @Override
    public void characters(char buf[], int offset, int length) {
      if (current != null) {
        Node n = doc.createTextNode(new String(buf, offset, length));
        setLocationData(n);
        current.appendChild(n);
      }
    }
  }
}
