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
