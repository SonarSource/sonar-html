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
package org.sonar.plugins.html.checks.attributes;

import java.io.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

import static org.assertj.core.api.Assertions.assertThat;

public class RequiredAttributeCheckTest {

  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  public void detected() {
    assertThat(new RequiredAttributeCheck().attributes).isEmpty();
  }

  @Test
  public void custom() {
    RequiredAttributeCheck check = new RequiredAttributeCheck();
    check.attributes = "img.alt,script.type";

    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/RequiredAttributeCheck.html"), check);

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(2).withMessage("Add the missing \"alt\" to element \"img\".")
        .next().atLine(7).withMessage("Add the missing \"type\" to element \"script\".");
  }

  @Test
  public void checkRepetition() {
    RequiredAttributeCheck check = new RequiredAttributeCheck();
    check.attributes = "img.alt,script.type";

    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/RequiredAttributeCheck.html"), check);

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(2).withMessage("Add the missing \"alt\" to element \"img\".")
      .next().atLine(7).withMessage("Add the missing \"type\" to element \"script\".");

    sourceCode = TestHelper.scan(new File("src/test/resources/checks/RequiredAttributeCheck.html"), check);

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(2).withMessage("Add the missing \"alt\" to element \"img\".")
      .next().atLine(7).withMessage("Add the missing \"type\" to element \"script\".");
  }

}
