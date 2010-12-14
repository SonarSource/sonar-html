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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.utils.SonarException;
import org.sonar.api.utils.WildcardPattern;
import org.sonar.check.IsoCategory;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.visitor.WebSourceCode;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

@Rule(key = "XmlSchemaCheck", name = "XML Schema Check", description = "XML Schema Check", priority = Priority.CRITICAL,
    isoCategory = IsoCategory.Reliability)
public class XmlSchemaCheck extends AbstractPageCheck {

  private static final class LocalResourceResolver implements LSResourceResolver {

    private LSInput createLSInput(InputStream inputStream) {
      if (inputStream != null) {
        System.setProperty(DOMImplementationRegistry.PROPERTY, "org.apache.xerces.dom.DOMImplementationSourceImpl");

        try {
          DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
          DOMImplementation impl = registry.getDOMImplementation("XML 1.0 LS 3.0");
          DOMImplementationLS implls = (DOMImplementationLS) impl;
          LSInput lsInput = implls.createLSInput();
          lsInput.setByteStream(inputStream);
          return lsInput;
        } catch (ClassCastException e) {
          throw new SonarException();
        } catch (ClassNotFoundException e) {
          throw new SonarException();
        } catch (InstantiationException e) {
          throw new SonarException();
        } catch (IllegalAccessException e) {
          throw new SonarException();
        }
      }
      return null;
    }

    public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {

      LOG.warn("resolveResource: " + systemId);

      if (StringUtils.contains(systemId, "/")) {
        return createLSInput(Schemas.getSchemaByNamespace(systemId));
      } else {
        return createLSInput(Schemas.getSchemaByFileName(systemId));
      }
    }
  }

  private class MessageHandler implements ErrorHandler {

    public void error(SAXParseException e) throws SAXException {
      createViolation(e.getLineNumber(), e.getLocalizedMessage());
    }

    public void fatalError(SAXParseException e) throws SAXException {
      error(e);
    }

    public void warning(SAXParseException e) throws SAXException {
      error(e);
    }
  }

  private static final Logger LOG = LoggerFactory.getLogger(XmlSchemaCheck.class);

  @RuleProperty(key = "antPattern", description = "antPattern")
  private String antPattern;

  @RuleProperty(key = "schemaLocation", description = "Schema Location")
  private String schemaLocation;

  private Schema createSchema() {
    SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    schemaFactory.setResourceResolver(new LocalResourceResolver());

    List<Source> schemaSources = new ArrayList<Source>();
    String[] schemaList = StringUtils.split(schemaLocation, " \t\n");
    for (int i = 0; i < schemaList.length - 1; i += 2) {
      String namespace = schemaList[i];
      String schemaFile = schemaList[i + 1];

      InputStream input = Schemas.getSchemaByNamespace(namespace);
      if (input != null) {
        schemaSources.add(new StreamSource(input));
      } else {
        try {
          schemaSources.add(new StreamSource(new FileInputStream(schemaFile)));
        } catch (FileNotFoundException e) {
          throw new SonarException();
        }
      }
    }

    try {
      return schemaFactory.newSchema(schemaSources.toArray(new Source[0]));
    } catch (SAXException e) {
      throw new SonarException(e);
    }
  }

  public String getSchemaLocation() {
    return schemaLocation;
  }

  private boolean matchFilePattern(String fileName) {
    WildcardPattern matcher = WildcardPattern.create(antPattern, "/");
    return matcher.match(fileName);
  }

  public void setSchemaLocation(String schemaLocation) {
    this.schemaLocation = schemaLocation;
  }

  @Override
  public void startDocument(WebSourceCode webSourceCode) {
    super.startDocument(webSourceCode);

    if (schemaLocation != null) {
      validate();
    }
  }

  private void validate() {
    Validator validator = createSchema().newValidator();
    validator.setErrorHandler(new MessageHandler());
    validator.setResourceResolver(new LocalResourceResolver());
    try {
      validator.validate(new StreamSource(getWebSourceCode().getInputStream()));
    } catch (SAXException e) {
      createViolation(0, e.getMessage());
    } catch (IOException e) {
      throw new SonarException(e);
    }
  }
}
