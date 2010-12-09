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
import java.io.PrintStream;
import java.io.StringReader;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.lang.StringUtils;
import org.sonar.api.utils.SonarException;
import org.sonar.check.IsoCategory;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.visitor.WebSourceCode;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

@Rule(key = "XmlSchemaCheck", name = "XML Schema Check", description = "XML Schema Check", priority = Priority.CRITICAL,
    isoCategory = IsoCategory.Reliability)
public class XmlSchemaCheck extends AbstractPageCheck {

  @RuleProperty(key = "schemas")
  private String[] schemas;

  public String getSchemas() {
    if (schemas != null) {
      return StringUtils.join(schemas, ",");
    }
    return "";
  }

  public void setSchemas(String list) {
    schemas = StringUtils.split(list, ",");

    validator = createValidator();
  }

  private Validator createValidator() {
    for (String schemaFile : schemas) {
      SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      Schema schema;
      try {
        schema = schemaFactory.newSchema(new File(schemaFile));
        return schema.newValidator();
      } catch (SAXException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return null;
  }

  private Validator createValidator2() {

    StringBuilder sb = new StringBuilder();
    sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n");
    sb.append("<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"unqualified\">\n");
    for (String schema : schemas) {
      sb.append("<xs:include schemaLocation=\"" + schema + "\"/>\n");
    }
    sb.append("</xs:schema>");
    try {
      SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      Schema schema = schemaFactory.newSchema(new StreamSource(new StringReader(sb.toString()), "xsdTop"));
      return schema.newValidator();
    } catch (SAXException e) {
      throw new SonarException();
    }
  }

  private Validator validator;

  @Override
  public void startDocument(WebSourceCode webSourceCode) {
    super.startDocument(webSourceCode);

    if (validator != null) {
      validate();
    }
  }

  static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";

  private void validate() {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setValidating(false);
    try {
      factory.setFeature("http://xml.org/sax/features/validation", true);
//      factory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
//      factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
//      factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
//      factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

    } catch (ParserConfigurationException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }

    File file = new File(getWebSourceCode().getResource().getKey());

    try {
      SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      Schema schema = schemaFactory.newSchema(new File(schemas[0]));
      factory.setSchema(schema);
      factory.newDocumentBuilder().parse(file);
    } catch (SAXException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (ParserConfigurationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private void validate2() {
    try {
      File file = new File(getWebSourceCode().getResource().getKey());

      Document document = new XmlParser().createDomDocument(file);
      DOMResult result = new DOMResult();
      ErrorHandler errorHandler = new ErrorHandler() {

        public void warning(SAXParseException e) throws SAXException {
          PrintStream out = System.out;
          out.print(e.getLineNumber() + ": ");
          out.println(e.getLocalizedMessage() + "\n");
        }

        public void fatalError(SAXParseException e) throws SAXException {
          PrintStream out = System.out;
          out.print(e.getLineNumber() + ": ");
          out.println(e.getLocalizedMessage() + "\n");
        }

        public void error(SAXParseException e) throws SAXException {
          PrintStream out = System.out;
          out.print(e.getLineNumber() + ": ");
          out.println(e.getLocalizedMessage() + "\n");
        }
      };
      validator.setErrorHandler(errorHandler);
      validator.validate(new DOMSource(document), result);
    } catch (SAXException e) {
      PrintStream out = System.out;
      out.println(e.getLocalizedMessage() + "\n");
      throw new SonarException(e);
    } catch (IOException e) {
      throw new SonarException(e);
    }
  }
}
