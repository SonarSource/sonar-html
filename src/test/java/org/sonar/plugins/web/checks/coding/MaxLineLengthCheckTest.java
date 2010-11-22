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

import static junit.framework.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.StringReader;

import org.junit.Test;
import org.sonar.plugins.web.checks.AbstractCheckTester;
import org.sonar.plugins.web.checks.coding.MaxLineLengthCheck;
import org.sonar.plugins.web.visitor.WebSourceCode;

/**
 * @author Matthijs Galesloot
 */
public class MaxLineLengthCheckTest extends AbstractCheckTester {

  @Test
  public void violateMaxLengthCheck() throws FileNotFoundException {

    String fragment = "<td><br><tr>";
    StringBuilder sb = new StringBuilder();

    for (int i = 0; i < 12; i++) {
      sb.append(fragment);
    }
    WebSourceCode sourceCode = parseAndCheck(new StringReader(sb.toString()), MaxLineLengthCheck.class);
    int numViolations = 1;
    assertTrue("Should have found " + numViolations + " violations", sourceCode.getViolations().size() == numViolations);
  }

  @Test
  public void passMaxLengthCheck() throws FileNotFoundException {

    String fragment = "<td><br><tr>";
    StringBuilder sb = new StringBuilder();

    for (int i = 0; i < 12; i++) {
      sb.append(fragment);
      sb.append("\n");
    }
    WebSourceCode sourceCode = parseAndCheck(new StringReader(sb.toString()), MaxLineLengthCheck.class);
    int numViolations = 0;
    assertTrue("Should have found " + numViolations + " violations", sourceCode.getViolations().size() == numViolations);
  }
}
