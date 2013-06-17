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
package org.sonar.plugins.web.checks.structure;

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
public class ParentElementRequiredCheckTest extends AbstractCheckTester {

  @Test
  public void violateParentRequiredCheck() throws FileNotFoundException {

    String fragment = "<illegalparent><title>Hello</title></illegalparent>";
    Reader reader = new StringReader(fragment);
    WebSourceCode sourceCode = parseAndCheck(reader, ParentElementRequiredCheck.class,
        "parent", "head", "child", "title");

    assertEquals("Incorrect number of violations", 1, sourceCode.getViolations().size());
  }

  @Test
  public void passParentRequiredCheck() throws FileNotFoundException {

    String fragment = "<head><title>Hello</title></head>";
    Reader reader = new StringReader(fragment);
    WebSourceCode sourceCode = parseAndCheck(reader, ParentElementRequiredCheck.class,
        "parent", "head", "child", "title");

    assertEquals("Incorrect number of violations", 0, sourceCode.getViolations().size());
  }
}
