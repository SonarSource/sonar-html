/*
 * Sonar Web Plugin
 * Copyright (C) 2010 Matthijs Galesloot and SonarSource
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
package org.sonar.plugins.web.checks.dependencies;

import org.junit.Test;
import org.sonar.plugins.web.checks.AbstractCheckTester;
import org.sonar.plugins.web.visitor.WebSourceCode;

import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.StringReader;

import static junit.framework.Assert.assertEquals;

/**
 * @author Matthijs Galesloot
 */
public class IllegalNamespaceCheckTest extends AbstractCheckTester {

  private static final String fragment = "<html xmlns=\"http://www.w3.org/1999/xhtml\"\n"
    + "xmlns:c=\"http://java.sun.com/jstl/core\"\n"
    + "xmlns:rich=\"http://richfaces.org/rich\"\n";

  @Test
  public void violateIllegalNamespaceCheck() throws FileNotFoundException {

    Reader reader = new StringReader(fragment);
    WebSourceCode sourceCode = parseAndCheck(reader, IllegalNamespaceCheck.class,
        "namespaces", "http://richfaces.org/rich,http://java.sun.com/jstl/core");

    assertEquals("Incorrect number of violations", 2, sourceCode.getViolations().size());

    reader = new StringReader(fragment);
    sourceCode = parseAndCheck(reader, IllegalNamespaceCheck.class,
        "namespaces", "http://richfaces.org/rich");

    assertEquals("Incorrect number of violations", 1, sourceCode.getViolations().size());
  }

  @Test
  public void passIllegalNamespaceCheck() throws FileNotFoundException {

    Reader reader = new StringReader(fragment);
    WebSourceCode sourceCode = parseAndCheck(reader, IllegalNamespaceCheck.class,
        "namespaces", "http://java.sun.com/jsf/html");

    assertEquals("Incorrect number of violations", 0, sourceCode.getViolations().size());
  }
}
