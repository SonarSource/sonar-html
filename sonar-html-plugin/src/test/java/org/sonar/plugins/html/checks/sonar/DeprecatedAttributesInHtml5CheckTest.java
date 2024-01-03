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
package org.sonar.plugins.html.checks.sonar;


import java.io.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

public class DeprecatedAttributesInHtml5CheckTest {

  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  public void test() {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/deprecatedAttributesInHtml5.html"), new DeprecatedAttributesInHtml5Check());

    this.checkMessagesVerifier.verify(sourceCode.getIssues())
            .next().atLocation(3, 2, 3, 209).withMessage("Remove this deprecated \"CHARSET\" attribute.")
            .next().atLine(3).withMessage("Remove this deprecated \"COORDS\" attribute.")
            .next().atLine(3).withMessage("Remove this deprecated \"NAME\" attribute.")
            .next().atLine(3).withMessage("Remove this deprecated \"SHAPE\" attribute.")
            .next().atLine(6).withMessage("Remove this deprecated \"bordercolor\" attribute.")
            .next().atLine(15).withMessage("Remove this deprecated \"code\" attribute.")
            .next().atLine(18).withMessage("Remove this deprecated \"border\" attribute.")
            .next().atLine(20).withMessage("Remove this deprecated \"name\" attribute.")
            .next().atLine(22).withMessage("Remove this deprecated \"name\" attribute.")
            .next().atLine(25).withMessage("Remove this deprecated \"align\" attribute.")
            .next().atLine(27).withMessage("Remove this deprecated \"language\" attribute.")
            .next().atLine(32).withMessage("Remove this deprecated \"[border]\" attribute.")
            .next().atLine(33).withMessage("Remove this deprecated \"[datafld]\" attribute.")
            .next().atLine(35).withMessage("Remove this deprecated \"[attr.datafld]\" attribute.")
            .next().atLine(36).withMessage("Remove this deprecated \"attr.datafld\" attribute.");
  }

}
