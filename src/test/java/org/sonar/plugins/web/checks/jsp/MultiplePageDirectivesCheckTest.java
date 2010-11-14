/*
 * Sonar Web Plugin
 * Copyright (C) 2010 Matthijs Galesloot
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.plugins.web.checks.jsp;

import static junit.framework.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.StringReader;

import org.junit.Test;
import org.sonar.plugins.web.checks.AbstractCheckTester;
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
