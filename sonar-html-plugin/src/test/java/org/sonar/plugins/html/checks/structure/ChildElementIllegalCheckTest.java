/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (c) 2010-2023 SonarSource SA and Matthijs Galesloot
 * sonarqube@googlegroups.com
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
package org.sonar.plugins.html.checks.structure;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class ChildElementIllegalCheckTest {

  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  public void detected() {
    ChildElementIllegalCheck check = new ChildElementIllegalCheck();
    assertThat(check.child).isEmpty();
    assertThat(check.parent).isEmpty();
  }

  @Test
  public void custom() {
    ChildElementIllegalCheck check = new ChildElementIllegalCheck();
    check.child = "bar";
    check.parent = "foo";

    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/ChildElementIllegalCheck.html"), check);

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLocation(2, 2, 2, 7).withMessage("Remove this \"bar\" tag; it is not a legal child of \"foo\".");
  }

}
