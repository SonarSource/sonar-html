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

package org.sonar.plugins.web.checks.scripting;

import static junit.framework.Assert.assertEquals;

import java.io.StringReader;

import org.junit.Test;
import org.sonar.plugins.web.checks.AbstractCheckTester;
import org.sonar.plugins.web.visitor.WebSourceCode;

/**
 * @author Matthijs Galesloot
 */
public class OGNLExpressionCheckTest extends AbstractCheckTester {

  @Test
  public void testSimple() {
    String fragment = "<radio empty list='#{myString.length}'  />";
    StringReader reader = new StringReader(fragment);
    WebSourceCode sourceCode = parseAndCheck(reader, OGNLExpressionCheck.class);

    assertEquals("Incorrect number of violations", 0, sourceCode.getViolations().size());
  }

  @Test
  public void testViolatedExpression() {
    String fragment = "<radio empty='' list='#{myString..length}' />";
    StringReader reader = new StringReader(fragment);
    WebSourceCode sourceCode = parseAndCheck(reader, OGNLExpressionCheck.class);

    assertEquals("Incorrect number of violations", 1, sourceCode.getViolations().size());
  }

  @Test
  public void testJSTL() {
    String fragment = "<radio empty='' list='${pageContext.request.contextPath}' />";
    StringReader reader = new StringReader(fragment);
    WebSourceCode sourceCode = parseAndCheck(reader, OGNLExpressionCheck.class);

    assertEquals("Incorrect number of violations", 0, sourceCode.getViolations().size());
  }


  @Test
  public void testPercentage() {
    String fragment = "<s:hidden value='%{myString..length}' value2=\"%{'name2'}\" value3='%{#request.foo}'/>";
    StringReader reader = new StringReader(fragment);
    WebSourceCode sourceCode = parseAndCheck(reader, OGNLExpressionCheck.class);

    assertEquals("Incorrect number of violations", 1, sourceCode.getViolations().size());
  }

  @Test
  public void lambda() {
    String fragment = "<s:hidden value='#fib =:[#this==0 ? 0 : #this==1 ? 1 : #fib(#this-2)+#fib(#this-1)], #fib(11)' />";
    StringReader reader = new StringReader(fragment);
    WebSourceCode sourceCode = parseAndCheck(reader, OGNLExpressionCheck.class);

    assertEquals("Incorrect number of violations", 0, sourceCode.getViolations().size());
  }

}
