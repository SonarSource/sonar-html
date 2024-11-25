/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (C) 2010-2024 SonarSource SA and Matthijs Galesloot
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
package org.sonar.plugins.html.checks.dependencies;

import java.io.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

import static org.assertj.core.api.Assertions.assertThat;

public class IllegalNamespaceCheckTest {

  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  public void detected() {
    assertThat(new IllegalNamespaceCheck().namespaces).isEmpty();
  }

  @Test
  public void custom() {
    IllegalNamespaceCheck check = new IllegalNamespaceCheck();
    check.namespaces = "foo,baz";

    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/IllegalNamespaceCheck.html"), check);

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(1).withMessage("Using \"baz\" namespace is not allowed.")
      .next().atLine(1).withMessage("Using \"foo\" namespace is not allowed.")
      .next().atLine(6).withMessage("Using \"foo\" namespace is not allowed.");
  }

}
