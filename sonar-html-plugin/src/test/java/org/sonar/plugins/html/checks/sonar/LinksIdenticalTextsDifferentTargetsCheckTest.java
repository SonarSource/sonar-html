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
package org.sonar.plugins.html.checks.sonar;


import java.io.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

class LinksIdenticalTextsDifferentTargetsCheckTest {

  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  void detected() {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/LinksIdenticalTextsDifferentTargetsCheck.html"), new LinksIdenticalTextsDifferentTargetsCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(5)
        .next().atLine(8)
        .next().atLine(11)
        .next().atLine(15)
        .next().atLine(23)
        .next().atLine(38)
        .next().atLine(44)
        .next().atLine(56).withMessage("Use distinct texts or point to the same target for this link and the one at line 55.")
        .next().atLine(57).withMessage("Use distinct texts or point to the same target for this link and the one at line 56.")
        .next().atLine(93).withMessage("Use distinct texts or point to the same target for this link and the one at line 92.")
        .next().atLine(109).withMessage("Use distinct texts or point to the same target for this link and the one at line 107.")
        .next().atLine(118).withMessage("Use distinct texts or point to the same target for this link and the one at line 116.")
        .next().atLine(137).withMessage("Use distinct texts or point to the same target for this link and the one at line 136.")
        .next().atLine(151).withMessage("Use distinct texts or point to the same target for this link and the one at line 150.")
        .next().atLine(163).withMessage("Use distinct texts or point to the same target for this link and the one at line 162.")
        .next().atLine(188).withMessage("Use distinct texts or point to the same target for this link and the one at line 187.")
        .noMore();
  }

  @Test
  void razor() {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/LinksIdenticalTextsDifferentTargetsCheck.cshtml"), new LinksIdenticalTextsDifferentTargetsCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(23).withMessage("Use distinct texts or point to the same target for this link and the one at line 20.")
        .next().atLine(33).withMessage("Use distinct texts or point to the same target for this link and the one at line 31.")
        .next().atLine(46).withMessage("Use distinct texts or point to the same target for this link and the one at line 44.")
        .next().atLine(54).withMessage("Use distinct texts or point to the same target for this link and the one at line 53.")
        .next().atLine(63).withMessage("Use distinct texts or point to the same target for this link and the one at line 62.")
        .next().atLine(64).withMessage("Use distinct texts or point to the same target for this link and the one at line 62.")
        .noMore();
  }

}
