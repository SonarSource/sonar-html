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

import org.junit.Test;
import org.sonar.plugins.web.checks.AbstractCheckTester;
import org.sonar.plugins.web.visitor.WebSourceCode;

import java.io.StringReader;

import static junit.framework.Assert.assertEquals;

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
    String fragment = "<hidden value='%{myString..length}' value2=\"%{'name2'}\" value3='%{#request.foo}'/>";
    StringReader reader = new StringReader(fragment);
    WebSourceCode sourceCode = parseAndCheck(reader, OGNLExpressionCheck.class);

    assertEquals("Incorrect number of violations", 1, sourceCode.getViolations().size());
  }

  @Test
  public void testMultipleExpressions() {
    String fragment = "<hidden value = '%{foo wrong}-%{bar + fault}'/>";
    StringReader reader = new StringReader(fragment);
    WebSourceCode sourceCode = parseAndCheck(reader, OGNLExpressionCheck.class);

    assertEquals("Incorrect number of violations", 1, sourceCode.getViolations().size());
  }

  @Test
  public void testCorrectMultipleExpressions() {
    String fragment = "<hidden value = '%{foo.wrong}-%{bar.fault}'/>";
    StringReader reader = new StringReader(fragment);
    WebSourceCode sourceCode = parseAndCheck(reader, OGNLExpressionCheck.class);

    assertEquals("Incorrect number of violations", 0, sourceCode.getViolations().size());
  }

  @Test
  public void testEmptyExpressions() {
    String fragment = "<hidden value ='%{}' value2='#{}' />";
    StringReader reader = new StringReader(fragment);
    WebSourceCode sourceCode = parseAndCheck(reader, OGNLExpressionCheck.class);

    assertEquals("Incorrect number of violations", 2, sourceCode.getViolations().size());
  }

  @Test
  public void testBrokenExpressionMarkers() {
    // the first two values are wrong, the third is fine.
    String fragment = "<hidden value ='%{' value2='#{' value3='#' />";
    StringReader reader = new StringReader(fragment);
    WebSourceCode sourceCode = parseAndCheck(reader, OGNLExpressionCheck.class);

    assertEquals("Incorrect number of violations", 2, sourceCode.getViolations().size());
  }

  @Test
  public void testNestedExpression() {
    // this should be OK
    String fragment = "<hidden valueLink=\"%{'${projectGroupSummaryUrl}'}\"/>";
    StringReader reader = new StringReader(fragment);
    WebSourceCode sourceCode = parseAndCheck(reader, OGNLExpressionCheck.class);

    assertEquals("Incorrect number of violations", 0, sourceCode.getViolations().size());
  }

  @Test
  public void lambda() {
    String fragment = "<hidden value='#fib =:[#this==0 ? 0 : #this==1 ? 1 : #fib(#this-2)+#fib(#this-1)], #fib(11)' />";
    StringReader reader = new StringReader(fragment);
    WebSourceCode sourceCode = parseAndCheck(reader, OGNLExpressionCheck.class);

    assertEquals("Incorrect number of violations", 0, sourceCode.getViolations().size());
  }

}
