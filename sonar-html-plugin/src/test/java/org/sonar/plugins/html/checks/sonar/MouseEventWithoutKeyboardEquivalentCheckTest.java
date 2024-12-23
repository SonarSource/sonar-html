/*
 * SonarQube HTML Plugin :: Sonar Plugin
 * Copyright (C) 2010-2024 SonarSource SA
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

public class MouseEventWithoutKeyboardEquivalentCheckTest {

  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  public void detected() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/MouseEventWithoutKeyboardEquivalentCheck.html"), new MouseEventWithoutKeyboardEquivalentCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLocation(1, 0, 1, 32).withMessage("Add a 'onFocus' attribute to this <A> tag.")
        .next().atLine(2).withMessage("Add a 'onBlur' attribute to this <a> tag.")
        .next().atLine(14).withMessage("Add a 'onFocus' attribute to this <a> tag.")
        .next().atLine(15)
        .next().atLine(24)
        .next().atLine(25)
        .next().atLine(35)
        .next().atLine(36)
        .next().atLine(46)
        .next().atLine(51)
        .next().atLine(53)
        .next().atLine(54)
        .next().atLine(58).withMessage("Add a 'onKeyPress|onKeyDown|onKeyUp' attribute to this <div> tag.")
        .next().atLine(59)
        .next().atLine(65)
        .next().atLine(68)
        .next().atLine(70)
        .next().atLine(71)
        .next().atLine(79).withMessage("Add a 'onKeyPress|onKeyDown|onKeyUp' attribute to this <div> tag.")
    ;
  }
}
