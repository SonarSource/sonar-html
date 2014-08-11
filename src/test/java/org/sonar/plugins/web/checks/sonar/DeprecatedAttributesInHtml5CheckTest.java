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
package org.sonar.plugins.web.checks.sonar;


import org.junit.Rule;
import org.junit.Test;
import org.sonar.plugins.web.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.web.checks.TestHelper;
import org.sonar.plugins.web.visitor.WebSourceCode;

import java.io.File;

public class DeprecatedAttributesInHtml5CheckTest {

  @Rule
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  public void test() throws Exception {
    WebSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/deprecatedAttributesInHtml5.html"), new DeprecatedAttributesInHtml5Check());

    checkMessagesVerifier.verify(sourceCode.getViolations())
      .next().atLine(3).withMessage("Remove this deprecated \"CHARSET\" attribute.")
      .next().atLine(3).withMessage("Remove this deprecated \"COORDS\" attribute.")
      .next().atLine(3).withMessage("Remove this deprecated \"NAME\" attribute.")
      .next().atLine(3).withMessage("Remove this deprecated \"SHAPE\" attribute.")
      .next().atLine(6).withMessage("Remove this deprecated \"bordercolor\" attribute.")
      .next().atLine(15).withMessage("Remove this deprecated \"code\" attribute.")
      .next().atLine(18).withMessage("Remove this deprecated \"border\" attribute.")
      .next().atLine(20).withMessage("Remove this deprecated \"name\" attribute.")
      .next().atLine(22).withMessage("Remove this deprecated \"name\" attribute.")
      .next().atLine(25).withMessage("Remove this deprecated \"align\" attribute.")
      .next().atLine(27).withMessage("Remove this deprecated \"language\" attribute.");
  }

}
