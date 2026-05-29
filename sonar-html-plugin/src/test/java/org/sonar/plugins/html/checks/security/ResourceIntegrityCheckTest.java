/*
 * SonarQube HTML
 * Copyright (C) SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * You can redistribute and/or modify this program under the terms of
 * the Sonar Source-Available License Version 1, as published by SonarSource Sàrl.
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

class ResourceIntegrityCheckTest {

  private static final String MSG_MISSING_BOTH = "Add integrity and crossorigin=\"anonymous\" attributes to this element to enforce integrity checks.";
  private static final String MSG_MISSING_INTEGRITY = "Add an integrity attribute to this element to enforce integrity checks.";
  private static final String MSG_MISSING_CROSSORIGIN = "Add a crossorigin=\"anonymous\" attribute to this element to enforce integrity checks.";

  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  void test() {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/resourceIntegrityCheck.html"), new ResourceIntegrityCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      // versioned URL, both missing
      .next().atLine(2).withMessage(MSG_MISSING_BOTH)
      .next().atLine(3).withMessage(MSG_MISSING_BOTH)
      .next().atLine(4).withMessage(MSG_MISSING_BOTH)
      .next().atLine(5).withMessage(MSG_MISSING_BOTH)
      // package@version alias, both missing
      .next().atLine(8).withMessage(MSG_MISSING_BOTH)
      .next().atLine(9).withMessage(MSG_MISSING_BOTH)
      // versioned URL, only integrity missing
      .next().atLine(12).withMessage(MSG_MISSING_INTEGRITY)
      // versioned URL, only crossorigin missing
      .next().atLine(15).withMessage(MSG_MISSING_CROSSORIGIN)
      // link rel=stylesheet, versioned URL, both missing
      .next().atLine(46).withMessage(MSG_MISSING_BOTH)
      // link rel=stylesheet, versioned URL, only integrity missing
      .next().atLine(48).withMessage(MSG_MISSING_INTEGRITY)
      // link rel=stylesheet, versioned URL, only crossorigin missing
      .next().atLine(50).withMessage(MSG_MISSING_CROSSORIGIN)
      // link rel=preload, versioned URL, both missing
      .next().atLine(56).withMessage(MSG_MISSING_BOTH)
      // link rel=modulepreload, versioned URL, both missing
      .next().atLine(58).withMessage(MSG_MISSING_BOTH)
      .noMore();
  }

}
