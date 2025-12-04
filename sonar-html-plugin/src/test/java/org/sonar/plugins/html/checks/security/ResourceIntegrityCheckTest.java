/*
 * SonarQube HTML
 * Copyright (C) 2010-2025 SonarSource Sàrl
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
package org.sonar.plugins.html.checks.security;

import java.io.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

public class ResourceIntegrityCheckTest {

  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  public void test() {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/resourceIntegrityCheck.html"), new ResourceIntegrityCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLocation(1, 0, 1, 47).withMessage("Make sure not using resource integrity feature is safe here.")
      .next().atLocation(2, 0, 2, 41).withMessage("Make sure not using resource integrity feature is safe here.")
      .next().atLocation(3, 0, 3, 46).withMessage("Make sure not using resource integrity feature is safe here.")
      .next().atLocation(4, 0, 4, 61).withMessage("Make sure not using resource integrity feature is safe here.");
  }

}
