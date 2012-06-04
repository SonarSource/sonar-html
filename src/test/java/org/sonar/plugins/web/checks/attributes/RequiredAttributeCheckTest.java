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
package org.sonar.plugins.web.checks.attributes;

import org.junit.Test;
import org.sonar.plugins.web.checks.AbstractCheckTester;
import org.sonar.plugins.web.visitor.WebSourceCode;

import java.io.FileNotFoundException;
import java.io.StringReader;

import static junit.framework.Assert.assertEquals;

/**
 * @author Matthijs Galesloot
 */
public class RequiredAttributeCheckTest extends AbstractCheckTester {

  @Test
  public void violateRequiredAttributeCheck() throws FileNotFoundException {

    String fragment = "<img src=\"a.png\">";
    WebSourceCode sourceCode = parseAndCheck(new StringReader(fragment), RequiredAttributeCheck.class);

    assertEquals("Incorrect number of violations", 1, sourceCode.getViolations().size());

    fragment = "<script />";
    sourceCode = parseAndCheck(new StringReader(fragment), RequiredAttributeCheck.class);

    assertEquals("Incorrect number of violations", 1, sourceCode.getViolations().size());
  }

  @Test
  public void passRequiredTypeAttributeCheck() throws FileNotFoundException {

    String fragment = "<img src=\"a.png\" alt=\"hello\" >";
    WebSourceCode sourceCode = parseAndCheck(new StringReader(fragment), RequiredAttributeCheck.class);

    assertEquals("Incorrect number of violations", 0, sourceCode.getViolations().size());

    fragment = "<script type='javascript' />";
    sourceCode = parseAndCheck(new StringReader(fragment), RequiredAttributeCheck.class);

    assertEquals("Incorrect number of violations", 0, sourceCode.getViolations().size());
  }
}
