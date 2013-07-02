/*
 * Sonar Web Plugin
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
package org.sonar.plugins.web.checks.generic;

import org.junit.Rule;
import org.junit.Test;
import org.sonar.plugins.web.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.web.checks.sonar.TestHelper;
import org.sonar.plugins.web.visitor.WebSourceCode;

import java.io.File;

public class RegularExpressionCheckTest {

  @Rule
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  public void detected() {
    WebSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/RegularExpressionCheck.html"), new RegularExpressionCheck());

    checkMessagesVerifier.verify(sourceCode.getViolations());
  }

  @Test
  public void custom() {
    RegularExpressionCheck check = new RegularExpressionCheck();
    check.expression = "(?i)<br\\s*+>";

    WebSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/RegularExpressionCheck.html"), check);

    checkMessagesVerifier.verify(sourceCode.getViolations())
        .next().atLine(4).withMessage("This start tag matches the given regular expression.")
        .next().atLine(5)
        .next().atLine(6)
        .next().atLine(7);
  }

}
