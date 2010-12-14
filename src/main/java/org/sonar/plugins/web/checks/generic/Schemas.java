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

final class Schemas {

  private static final class SchemaFile {

    private final String fileName;
    private final String nameSpace;

    public SchemaFile(String nameSpace, String fileName) {
      this.nameSpace = nameSpace;
      this.fileName = fileName;
    }
  }

  private static final SchemaFile[] SCHEMA_FILES = new SchemaFile[] {
    new SchemaFile("http://www.w3.org/2001/xml.xsd", "xml.xsd"),
    new SchemaFile("http://www.w3.org/1999/xhtml", "xhtml1-transitional.xsd"),
    new SchemaFile("http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd", "xhtml1-transitional.dtd"),
    new SchemaFile("http://java.sun.com/jsf/html", "html-basic-2.0.xsd"),
    new SchemaFile("http://java.sun.com/jsf/facelets", "facelets-ui-2.0.xsd")
  };

  private static final String SCHEMA_PATH = "/schemas/";

  public static InputStream getSchemaByNamespace(String nameSpace) {
    for (SchemaFile schemaFile : SCHEMA_FILES) {
      if (nameSpace.equals(schemaFile.nameSpace)) {
        return getSchemaByFileName(schemaFile.fileName);
      }
    }
    return null;
  }

  public static InputStream getSchemaByFileName(String fileName) {
    return Schemas.class.getResourceAsStream(SCHEMA_PATH + fileName);
  }
}
