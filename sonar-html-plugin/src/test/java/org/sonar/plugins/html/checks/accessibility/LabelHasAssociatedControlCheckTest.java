/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (c) 2010-2024 SonarSource SA and Matthijs Galesloot
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
package org.sonar.plugins.html.checks.accessibility;

import java.io.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

public class LabelHasAssociatedControlCheckTest {
  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  void nesting() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(
            new File("src/test/resources/checks/LabelHasAssociatedControlCheck/nesting.html"),
            new LabelHasAssociatedControlCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
            .next().atLine(2).withMessage("A form label must be associated with a control.")
            .next().atLine(3)
            .next().atLine(5)
            .next().atLine(10)
            .noMore();
  }

  @Test
  void forAttribute() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(
            new File("src/test/resources/checks/LabelHasAssociatedControlCheck/" +
                    "for.html"),
            new LabelHasAssociatedControlCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
            .next().atLine(2).withMessage("A form label must be associated with a control.")
            .next().atLine(3)
            .next().atLine(6)
            .next().atLine(8)
            .next().atLine(10)
            .next().atLine(12)
            .noMore();
  }
}
