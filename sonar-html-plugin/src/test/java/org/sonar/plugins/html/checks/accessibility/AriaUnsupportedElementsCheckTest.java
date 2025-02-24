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
package org.sonar.plugins.html.checks.accessibility;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

class AriaUnsupportedElementsCheckTest {
    @RegisterExtension
    public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

    @Test
    void html() {
        HtmlSourceCode sourceCode = TestHelper.scan(
                new File("src/test/resources/checks/AriaUnsupportedElementsCheck.html"),
                new AriaUnsupportedElementsCheck());
        checkMessagesVerifier.verify(sourceCode.getIssues())
                .next().atLine(1).withMessage(
                        "This element does not support ARIA roles, states and properties. Try removing the prop aria-hidden.")
                .next().atLine(2)
                .noMore();
    }
}
