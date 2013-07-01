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
package org.sonar.plugins.web.checks.sonar;

import org.junit.Rule;
import org.junit.Test;
import org.sonar.plugins.web.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.web.visitor.WebSourceCode;

import java.io.File;

public class MouseEventWithoutKeyboardEquivalentCheckTest {

  @Rule
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  public void detected() throws Exception {
    WebSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/MouseEventWithoutKeyboardEquivalentCheck.html"), new MouseEventWithoutKeyboardEquivalentCheck());

    checkMessagesVerifier.verify(sourceCode.getViolations())
        .next().atLine(1).withMessage("Add a 'onKeyPress' attribute to this <a> tag.")
        .next().atLine(2).withMessage("Add a 'onFocus' attribute to this <A> tag.")
        .next().atLine(3).withMessage("Add a 'onBlur' attribute to this <a> tag.");
  }

}
