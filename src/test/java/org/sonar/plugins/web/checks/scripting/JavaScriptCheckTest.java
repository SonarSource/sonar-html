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

import java.io.FileNotFoundException;
import java.io.StringReader;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Matthijs Galesloot
 */
public class JavaScriptCheckTest extends AbstractCheckTester {

  @Test
  public void testJavaScriptCheck() throws FileNotFoundException {

    String fragment = "<h:someNode/><script language=\"JavaScript\">var a;\nvar b;\nvar c;\nvar d;\nvar e;\nvar f;\n</script>";

    WebSourceCode sourceCode = parseAndCheck(new StringReader(fragment), LongJavaScriptCheck.class);

    assertThat(sourceCode.getViolations().size()).isEqualTo(1);
  }
}
