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

package org.sonar.plugins.web.schemas;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.jfree.util.Log;
import org.sonar.api.utils.SonarException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;

public final class Schemas {

  private static final class SchemaFile {

    private final String fileName;
    private final String nameSpace;

    public SchemaFile(String nameSpace, String fileName) {
      this.nameSpace = nameSpace;
      this.fileName = fileName;
    }
  }

  private static final SchemaFile[] SCHEMA_FILES = new SchemaFile[] {
      // XML
      new SchemaFile("http://www.w3.org/2001/xml.xsd", "xml.xsd"),

      // XHTML1
      new SchemaFile("xhtml1-transitional", "xhtml1/xhtml1-transitional.xsd"),
      new SchemaFile("xhtml1-strict", "xhtml1/xhtml1-strict.xsd"),
      new SchemaFile("xhtml1-frameset", "xhtml1/xhtml1-frameset.xsd"),

      // XHTML1 - DTD
      new SchemaFile("http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd", "xhtml1/xhtml1-transitional.dtd"),
      new SchemaFile("http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd", "xhtml1/xhtml1-strict.dtd"),
      new SchemaFile("http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd", "xhtml1/xhtml1-frameset.dtd"),

      // JSF Taglib to XSD conversions
      // (from http://blogger.ziesemer.com/2008/03/facelets-and-xsd-converted-tlds.html)
      new SchemaFile("http://java.sun.com/jsf/core", "jsf/jsf-core-2.0.xsd"),
      new SchemaFile("http://java.sun.com/jsf/html", "jsf/html-basic-2.0.xsd"),
      new SchemaFile("http://java.sun.com/jsf/facelets", "jsf/facelets-ui-2.0.xsd") };

  public static InputStream getSchemaByNamespace(String nameSpace) {
    for (SchemaFile schemaFile : SCHEMA_FILES) {
      if (nameSpace.equals(schemaFile.nameSpace)) {
        return getSchemaByFileName(schemaFile.fileName);
      }
    }
    return null;
  }

  private static final String[] FOLDERS = new String[] { "xhtml1", "jsf" };

  public static InputStream getSchemaByFileName(String fileName) {
    InputStream input = Schemas.class.getResourceAsStream(fileName);
    if (input == null) {
      for (String folder : FOLDERS) {
        input = Schemas.class.getResourceAsStream(folder + "/" + fileName);
        if (input != null) {
          break;
        }
      }
    }
    return input;
  }

  public static LSInput getSchemaAsLSInput(String systemId) {
    InputStream input;

    // try as namespace
    input = getSchemaByNamespace(systemId);

    // try as built-in resource
    if (input == null) {
      input = Schemas.getSchemaByFileName(systemId);

      // try as file system resource
      if (input == null) {
        try {
          input = new FileInputStream(systemId);
        } catch (FileNotFoundException e) {
          Log.debug("Could not find resource " + systemId);
          return null;
        }
      }
    }

    if (input != null) {
      return createLSInput(input);
    } else {
      return null;
    }
  }

  private static LSInput createLSInput(InputStream inputStream) {
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
        throw new SonarException(e);
      } catch (ClassNotFoundException e) {
        throw new SonarException(e);
      } catch (InstantiationException e) {
        throw new SonarException(e);
      } catch (IllegalAccessException e) {
        throw new SonarException();
      }
    }
    return null;
  }
}
