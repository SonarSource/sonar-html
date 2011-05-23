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

package org.sonar.plugins.web.checks.attributes;

import static junit.framework.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.StringReader;

import org.junit.Test;
import org.sonar.plugins.web.checks.AbstractCheckTester;
import org.sonar.plugins.web.visitor.WebSourceCode;

/**
 * @author Matthijs Galesloot
 */
public class AttributeValidationCheckTest extends AbstractCheckTester {


  @Test
  public void validateEmail() throws FileNotFoundException {

    String fragment = "<td email=\"a.png\">";
    WebSourceCode sourceCode = parseAndCheck(new StringReader(fragment), AttributeValidationCheck.class,
        "attributes", "email", "type", "email");

    assertEquals("Incorrect number of violations", 1, sourceCode.getViolations().size());

    fragment = "<td email=\"a@x.nl\">";
    sourceCode = parseAndCheck(new StringReader(fragment), AttributeValidationCheck.class,
        "attributes", "email", "type", "email");

    assertEquals("Incorrect number of violations", 0, sourceCode.getViolations().size());
  }

  @Test
  public void validateUrl() throws FileNotFoundException {

    String[] checkAttributes = new String[] { "attributes", "url", "type", "url" };

    String fragment = "<td url='httpd://aa'>";
    WebSourceCode sourceCode = parseAndCheck(new StringReader(fragment), AttributeValidationCheck.class,
        checkAttributes);

    assertEquals("Incorrect number of violations", 1, sourceCode.getViolations().size());

    fragment = "<td url='http://www.w3c.org/'>";
    sourceCode = parseAndCheck(new StringReader(fragment), AttributeValidationCheck.class,
        checkAttributes);

    assertEquals("Incorrect number of violations", 0, sourceCode.getViolations().size());
  }

  @Test
  public void validateCode() throws FileNotFoundException {

    String[] checkAttributes = new String[] { "attributes", "escape", "type", "code", "parameters", "true,ok"};

    String fragment = "<td escape='ok'>";
    WebSourceCode sourceCode = parseAndCheck(new StringReader(fragment), AttributeValidationCheck.class,
        checkAttributes);

    assertEquals("Incorrect number of violations", 0, sourceCode.getViolations().size());

    fragment = "<td escape='false'>";
    sourceCode = parseAndCheck(new StringReader(fragment), AttributeValidationCheck.class,
        checkAttributes);

    assertEquals("Incorrect number of violations", 1, sourceCode.getViolations().size());

    checkAttributes = new String[] { "attributes", "escape", "type", "code", "parameters", "t.*, o.*"};

    fragment = "<td escape='false'>";
    sourceCode = parseAndCheck(new StringReader(fragment), AttributeValidationCheck.class,
        checkAttributes);

    assertEquals("Incorrect number of violations", 1, sourceCode.getViolations().size());


  }
}
