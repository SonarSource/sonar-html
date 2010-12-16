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

import static junit.framework.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import org.junit.Test;
import org.sonar.plugins.web.checks.AbstractCheckTester;
import org.sonar.plugins.web.visitor.WebSourceCode;

/**
 * @author Matthijs Galesloot
 */
public class XmlSchemaCheckTest extends AbstractCheckTester {

  @Test
  public void violateLocalXmlSchemaCheck() throws FileNotFoundException {

    String fileName = "src/test/resources/checks/generic/catalog.xml";
    FileReader reader = new FileReader(fileName);
    WebSourceCode sourceCode = parseAndCheck(reader, new File(fileName), null,
        XmlSchemaCheck.class, "schemaLocation", "src/test/resources/checks/generic/catalog.xsd");

    assertEquals("Incorrect number of violations", 1, sourceCode.getViolations().size());
    assertEquals((Integer) 5, sourceCode.getViolations().get(0).getLineId());
  }

  @Test
  public void violateBuiltinXhtmlSchemaCheck() throws FileNotFoundException {
    String fileName = "src/test/resources/src/main/webapp/create-salesorder.xhtml";
    FileReader reader = new FileReader(fileName);
    WebSourceCode sourceCode = parseAndCheck(reader, new File(fileName), null,
        XmlSchemaCheck.class, "schemaLocation", "xhtml1-transitional" );

    assertEquals("Incorrect number of violations", 2, sourceCode.getViolations().size());
    assertEquals((Integer) 16, sourceCode.getViolations().get(0).getLineId());
  }

  @Test
  public void violateStrictHtml1heck() throws FileNotFoundException {
    String fileName = "src/test/resources/checks/generic/TenderNed - Aankondigingen.xhtml";
    FileReader reader = new FileReader(fileName);
    WebSourceCode sourceCode = parseAndCheck(reader, new File(fileName), null,
        XmlSchemaCheck.class, "schemaLocation", "xhtml1-strict" );

//    for (Violation v : sourceCode.getViolations()) {
//      PrintStream out = System.out;
//      out.println(v.getLineId() + ": " + v.getMessage());
//    }
    assertEquals("Incorrect number of violations", 143, sourceCode.getViolations().size());
    assertEquals((Integer) 60, sourceCode.getViolations().get(0).getLineId());
  }

  @Test
  public void violateJsfSchema() throws FileNotFoundException {
    String fileName = "src/test/resources/checks/generic/create-salesorder2.xhtml";
    FileReader reader = new FileReader(fileName);
    WebSourceCode sourceCode = parseAndCheck(reader, new File(fileName), null,
        XmlSchemaCheck.class, "schemaLocation", "http://java.sun.com/jsf/html" );

    assertEquals("Incorrect number of violations", 1, sourceCode.getViolations().size());
    assertEquals((Integer) 7, sourceCode.getViolations().get(0).getLineId());
  }

  @Test
  public void violateFaceletsSchema() throws FileNotFoundException {
    String fileName = "src/test/resources/checks/generic/create-salesorder2.xhtml";
    FileReader reader = new FileReader(fileName);
    WebSourceCode sourceCode = parseAndCheck(reader, new File(fileName), null,
        XmlSchemaCheck.class, "schemaLocation", "http://java.sun.com/jsf/core" );

    assertEquals("Incorrect number of violations", 0, sourceCode.getViolations().size());
    //assertEquals((Integer) , sourceCode.getViolations().get(0).getLineId());
  }

}
