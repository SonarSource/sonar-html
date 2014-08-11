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
package org.sonar.plugins.web.checks.coding;

import org.junit.Rule;
import org.junit.Test;
import org.sonar.plugins.web.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.web.checks.TestHelper;
import org.sonar.plugins.web.checks.whitespace.WhiteSpaceAroundCheck;
import org.sonar.plugins.web.visitor.WebSourceCode;

import java.io.File;

/**
 * @author Matthijs Galesloot
 */
public class WhiteSpaceAroundCheckTest {

  @Rule
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  public void test() throws Exception {
    WebSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/whiteSpaceAroundCheck.html"), new WhiteSpaceAroundCheck());

    checkMessagesVerifier.verify(sourceCode.getViolations())
      .next().atLine(1).withMessage("A whitespace is missing after the starting tag at column 4.")
      .next().atLine(1).withMessage("Add a space at column 18.")
      .next().atLine(3).withMessage("Add a space at column 18.")
      .next().atLine(5).withMessage("A whitespace is missing after the starting tag at column 2.")
      .next().atLine(7).withMessage("A whitespace is missing after the starting tag at column 3.")
      .next().atLine(9).withMessage("A whitespace is missing after the starting tag at column 3.")
      .next().atLine(11).withMessage("A whitespace is missing after the starting tag at column 3.")
      .next().atLine(13).withMessage("A whitespace is missing after the starting tag at column 4.")
      .next().atLine(13).withMessage("Add a space at column 18.");
  }

}
