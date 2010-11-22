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

package org.sonar.plugins.web.checks.header;

import static junit.framework.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.StringReader;

import org.junit.Test;
import org.sonar.plugins.web.checks.AbstractCheckTester;
import org.sonar.plugins.web.checks.header.MultiplePageDirectivesCheck;
import org.sonar.plugins.web.visitor.WebSourceCode;

public class MultiplePageDirectivesCheckTest extends AbstractCheckTester {

  @Test
  public void onlyOnePageDirectiveIsAllowed() throws FileNotFoundException {

    StringBuilder sb = new StringBuilder();
    sb.append("<h:someNode/>");
    sb.append("<%@ page session=\"false\" %>");
    sb.append("<%@ page errorPage=\"/common/errorPage.jsp\" %>");

    WebSourceCode sourceCode = parseAndCheck(new StringReader(sb.toString()), MultiplePageDirectivesCheck.class);

    assertTrue("Should have found 1 violation", sourceCode.getViolations().size() == 1);

    // add an import attribute to the page directive: show that the page directive will still be counted
    sb = new StringBuilder();
    sb.append("<h:someNode/>");
    sb.append("<%@ page session=\"false\" %>");
    sb.append("<%@ page errorPage=\"/common/errorPage.jsp\" import=\"java.util.*,java.text.*\" %>");

    sourceCode = parseAndCheck(new StringReader(sb.toString()), MultiplePageDirectivesCheck.class);

    assertTrue("Should have found 1 violation", sourceCode.getViolations().size() == 1);
  }

  @Test
  public void pageImportsAreOK() throws FileNotFoundException {

    // page import directives are not counted.
    StringBuilder sb = new StringBuilder();
    sb.append("<h:someNode/>");
    sb.append("<%@ page session=\"false\" %>");
    sb.append("<%@ page import=\"java.util.*,java.text.*\" %>");

    WebSourceCode sourceCode = parseAndCheck(new StringReader(sb.toString()), MultiplePageDirectivesCheck.class);

    assertTrue("Should have found 1 violation", sourceCode.getViolations().size() == 0);
  }
}
