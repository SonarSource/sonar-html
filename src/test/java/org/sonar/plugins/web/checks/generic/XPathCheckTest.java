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

import java.io.FileNotFoundException;
import java.io.FileReader;

import org.junit.Ignore;
import org.junit.Test;
import org.sonar.plugins.web.checks.AbstractCheckTester;
import org.sonar.plugins.web.visitor.WebSourceCode;

/**
 * @author Matthijs Galesloot
 */
@Ignore
public class XPathCheckTest extends AbstractCheckTester {

  @Test
  public void violateXPathCheck() throws FileNotFoundException {

    String fileName ="src/test/resources/src/main/webapp/create-salesorder.xhtml";
    FileReader reader = new FileReader(fileName);
    WebSourceCode sourceCode = parseAndCheck(reader, fileName, XPathCheck.class,
        "expression", "//br");

    assertEquals("Incorrect number of violations", 1, sourceCode.getViolations().size());
    assertEquals((Integer) 21, sourceCode.getViolations().get(0).getLineId());
  }

  @Test
  public void violateXPathWithNamespacesCheck() throws FileNotFoundException {
    String fileName ="src/test/resources/src/main/webapp/create-salesorder.xhtml";
    FileReader reader = new FileReader(fileName);
    WebSourceCode sourceCode = parseAndCheck(reader, fileName, XPathCheck.class,
        "expression", "//ui:define[@name='title']",
        "namespaces", "ui=\"http://java.sun.com/jsf/facelets\"");

    assertEquals("Incorrect number of violations", 1, sourceCode.getViolations().size());
    assertEquals((Integer) 26, sourceCode.getViolations().get(0).getLineId());
  }
}
