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

package org.sonar.plugins.web.checks.xhtml;

import static junit.framework.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.FileReader;

import org.junit.Test;
import org.sonar.plugins.web.checks.AbstractCheckTester;
import org.sonar.plugins.web.checks.structure.RequiredElementCheck;
import org.sonar.plugins.web.visitor.WebSourceCode;

/**
 * @author Matthijs Galesloot
 */
public class RequiredElementCheckTest extends AbstractCheckTester {

  @Test
  public void violateRequiredElement() throws FileNotFoundException {

    FileReader reader = new FileReader("src/test/resources/src/main/webapp/create-salesorder.xhtml");
    WebSourceCode sourceCode = parseAndCheck(reader, RequiredElementCheck.class, "elements", "html,notfound1,notfound2" );

    assertEquals("Incorrect number of violations", 2, sourceCode.getViolations().size());
  }
}
