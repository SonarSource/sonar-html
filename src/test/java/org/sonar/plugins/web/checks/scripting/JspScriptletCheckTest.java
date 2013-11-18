/*
 * SonarQube Web Plugin
 * Copyright (C) 2010 SonarSource and Matthijs Galesloot
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

import java.io.FileNotFoundException;
import java.io.StringReader;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Matthijs Galesloot
 */
public class JspScriptletCheckTest extends AbstractCheckTester {

  @Test
  public void violateJspScriptCheck() throws FileNotFoundException {

    String fragment = "<% line1\nline2;\nline3\nline4\nline5\n %>";
    WebSourceCode sourceCode = parseAndCheck(new StringReader(fragment), JspScriptletCheck.class);

    assertThat(sourceCode.getViolations()).hasSize(1);
  }

  @Test
  public void violateScriptletCheck() throws FileNotFoundException {

    String fragment = "<jsp:scriptlet>line1\nline2;\nline3\nline4\nline5\n</jsp:scriptlet>";
    WebSourceCode sourceCode = parseAndCheck(new StringReader(fragment), JspScriptletCheck.class);

    assertThat(sourceCode.getViolations()).hasSize(1);
  }

  @Test
  public void should_allow_empty_scriptlets() {
    String fragment = "<%\n%>";
    WebSourceCode sourceCode = parseAndCheck(new StringReader(fragment), JspScriptletCheck.class);

    assertThat(sourceCode.getViolations()).isEmpty();
  }

}
