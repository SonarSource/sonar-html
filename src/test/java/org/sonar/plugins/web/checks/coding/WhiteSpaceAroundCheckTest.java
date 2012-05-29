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

package org.sonar.plugins.web.checks.coding;

import org.junit.Test;
import org.sonar.plugins.web.checks.AbstractCheckTester;
import org.sonar.plugins.web.checks.whitespace.WhiteSpaceAroundCheck;
import org.sonar.plugins.web.visitor.WebSourceCode;

import java.io.FileNotFoundException;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;

/**
 * @author Matthijs Galesloot
 */
public class WhiteSpaceAroundCheckTest extends AbstractCheckTester {

  @Test
  public void violateWhiteSpaceAroundCheck() throws FileNotFoundException {

    String fragment = "<!--two violations-->";
    WebSourceCode sourceCode = parseAndCheck(new StringReader(fragment), WhiteSpaceAroundCheck.class);
    assertEquals(2, sourceCode.getViolations().size());

    fragment = "<!-- one violation-->";
    sourceCode = parseAndCheck(new StringReader(fragment), WhiteSpaceAroundCheck.class);
    assertEquals(1, sourceCode.getViolations().size());

    fragment = "<%one violations %>";
    sourceCode = parseAndCheck(new StringReader(fragment), WhiteSpaceAroundCheck.class);
    assertEquals(1, sourceCode.getViolations().size());

    fragment = "<%!one violations %>";
    sourceCode = parseAndCheck(new StringReader(fragment), WhiteSpaceAroundCheck.class);
    assertEquals(1, sourceCode.getViolations().size());

    fragment = "<%=one violations %>";
    sourceCode = parseAndCheck(new StringReader(fragment), WhiteSpaceAroundCheck.class);
    assertEquals(1, sourceCode.getViolations().size());

    fragment = "<%@one violations %>";
    sourceCode = parseAndCheck(new StringReader(fragment), WhiteSpaceAroundCheck.class);
    assertEquals(1, sourceCode.getViolations().size());

    fragment = "<%--two violations--%>";
    sourceCode = parseAndCheck(new StringReader(fragment), WhiteSpaceAroundCheck.class);
    assertEquals(2, sourceCode.getViolations().size());
  }

  @Test
  public void passWhiteSpaceAroundCheck() throws FileNotFoundException {

    String fragment = "<!-- zero violation -->";
    WebSourceCode sourceCode = parseAndCheck(new StringReader(fragment), WhiteSpaceAroundCheck.class);
    assertEquals(0, sourceCode.getViolations().size());

    fragment = "<% zero violations %>";
    sourceCode = parseAndCheck(new StringReader(fragment), WhiteSpaceAroundCheck.class);
    assertEquals(0, sourceCode.getViolations().size());

    fragment = "<%-- zero violations --%>";
    sourceCode = parseAndCheck(new StringReader(fragment), WhiteSpaceAroundCheck.class);
    assertEquals(0, sourceCode.getViolations().size());
  }
}
