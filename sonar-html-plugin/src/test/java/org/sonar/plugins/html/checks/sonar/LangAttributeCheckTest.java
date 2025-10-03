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

class LangAttributeCheckTest {

  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  void detected() {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/LangAttributeCheck.html"), new LangAttributeCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLocation(1, 0, 1, 6)
      .next().atLocation(3, 0, 3, 39).withMessage("Add \"lang\" and/or \"xml:lang\" attributes to this \"<html>\" element")
	    .next().atLine(23).withMessage("Add \"lang\" and/or \"xml:lang\" attributes to this \"<html>\" element")
	    .next().atLine(25).withMessage(LangAttributeCheck.DEFAULT_MESSAGE)
	    .next().atLine(28).withMessage(LangAttributeCheck.DEFAULT_MESSAGE)
	    .next().atLine(32).withMessage(LangAttributeCheck.DEFAULT_MESSAGE)
	    .next().atLine(34).withMessage(LangAttributeCheck.DEFAULT_MESSAGE)
	    .next().atLine(36).withMessage(LangAttributeCheck.DEFAULT_MESSAGE)
	    .next().atLine(38).withMessage(LangAttributeCheck.DEFAULT_MESSAGE)
	    .next().atLine(39).withMessage(LangAttributeCheck.DEFAULT_MESSAGE)
	    .next().atLocation(41, 8, 41, 35).withMessage(LangAttributeCheck.DEFAULT_MESSAGE)
	    .next().atLocation(41, 35, 41, 43).withMessage(LangAttributeCheck.DEFAULT_MESSAGE)
	    .next().atLine(43).withMessage(LangAttributeCheck.DEFAULT_MESSAGE)
			.next().atLine(48).withMessage("Add \"lang\" and/or \"xml:lang\" attributes to this \"<html>\" element")
			.next().atLine(56).withMessage(LangAttributeCheck.DEFAULT_MESSAGE)
			.next().atLine(63).withMessage(LangAttributeCheck.DEFAULT_MESSAGE)
			.next().atLine(66).withMessage(LangAttributeCheck.DEFAULT_MESSAGE)
			.next().atLine(70).withMessage(LangAttributeCheck.DEFAULT_MESSAGE)
			.next().atLine(74).withMessage(LangAttributeCheck.DEFAULT_MESSAGE)
			.next().atLine(81).withMessage(LangAttributeCheck.DEFAULT_MESSAGE)
	    .noMore();
  }
}
