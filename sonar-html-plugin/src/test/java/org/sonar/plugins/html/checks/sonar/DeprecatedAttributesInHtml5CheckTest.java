/*
 * SonarQube HTML
 * Copyright (C) 2010-2025 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package org.sonar.plugins.html.checks.sonar;


import java.io.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

class DeprecatedAttributesInHtml5CheckTest {

  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  void test() {
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
