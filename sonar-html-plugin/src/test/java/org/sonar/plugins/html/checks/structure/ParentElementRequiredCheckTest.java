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
package org.sonar.plugins.html.checks.structure;

import java.io.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

import static org.assertj.core.api.Assertions.assertThat;

class ParentElementRequiredCheckTest {

  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  void detected() {
    ParentElementRequiredCheck check = new ParentElementRequiredCheck();
    assertThat(check.child).isEmpty();
    assertThat(check.parent).isEmpty();
  }

  @Test
  void custom() {
    ParentElementRequiredCheck check = new ParentElementRequiredCheck();
    check.child = "bar";
    check.parent = "foo";

    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/ParentElementRequiredCheck.html"), check);

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLocation(6, 0, 6, 5).withMessage("Add the missing \"foo\" parent element for \"bar\" element.")
        .next().atLine(17);
  }

}
