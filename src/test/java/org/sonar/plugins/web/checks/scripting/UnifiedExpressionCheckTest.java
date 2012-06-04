/*
 * Sonar Web Plugin
 * Copyright (C) 2010 Matthijs Galesloot
 * dev@sonar.codehaus.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;

import static junit.framework.Assert.assertEquals;

/**
 * @author Matthijs Galesloot
 */
public class UnifiedExpressionCheckTest extends AbstractCheckTester {

  @Test
  public void violateUnifiedExpressionCheck() throws FileNotFoundException {

    String fragment = "<tag expression=\"#{bean.getExpression() => 0} \"";

    StringReader reader = new StringReader(fragment);
    WebSourceCode sourceCode = parseAndCheck(reader, UnifiedExpressionCheck.class);
    assertEquals("Incorrect number of violations", 1, sourceCode.getViolations().size());
  }

  @Test
  public void testUnifiedExpressionCheck() throws FileNotFoundException {

    FileReader reader = new FileReader("src/test/resources/src/main/webapp/create-salesorder.xhtml");
    WebSourceCode sourceCode = parseAndCheck(reader, UnifiedExpressionCheck.class);

    assertEquals("Incorrect number of violations", 0, sourceCode.getViolations().size());
  }

  @Test
  public void escapeCharacters() {
    String fragment = "<c:when test=\"${citaflagurge eq \\\"S\\\"}\">";
    StringReader reader = new StringReader(fragment);
    WebSourceCode sourceCode = parseAndCheck(reader, UnifiedExpressionCheck.class);

    assertEquals("Incorrect number of violations", 0, sourceCode.getViolations().size());
  }

  @Test
  public void testValidFunction() {

    String fragment = "<TD align=\"${testUI:align('inverse')}\">";
    StringReader reader = new StringReader(fragment);
    WebSourceCode sourceCode = parseAndCheck(reader, UnifiedExpressionCheck.class, "functions", "align");

    assertEquals("Incorrect number of violations", 0, sourceCode.getViolations().size());
  }

  @Test
  public void testUnknownFunction() {

    String fragment = "<TD align=\"${testUI:align('inverse')}\">";
    StringReader reader = new StringReader(fragment);
    WebSourceCode sourceCode = parseAndCheck(reader, UnifiedExpressionCheck.class, "functions", "another-function");

    assertEquals("Incorrect number of violations", 1, sourceCode.getViolations().size());
  }
}
