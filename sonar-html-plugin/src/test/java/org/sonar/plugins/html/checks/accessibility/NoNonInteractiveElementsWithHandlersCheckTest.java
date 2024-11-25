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
package org.sonar.plugins.html.checks.accessibility;

import java.io.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

class NoNonInteractiveElementsWithHandlersCheckTest {

  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  void test() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(
      new File("src/test/resources/checks/NoNonInteractiveElementsWithHandlersCheck.html"),
      new NoNonInteractiveElementsWithHandlersCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(219).withMessage("Non-interactive elements should not be assigned mouse or keyboard event listeners.")
      .next().atLine(220)
      .next().atLine(221)
      .next().atLine(222)
      .next().atLine(223)
      .next().atLine(224)
      .next().atLine(225)
      .next().atLine(226)
      .next().atLine(227)
      .next().atLine(228)
      .next().atLine(229)
      .next().atLine(230)
      .next().atLine(231)
      .next().atLine(232)
      .next().atLine(233)
      .next().atLine(234)
      .next().atLine(235)
      .next().atLine(236)
      .next().atLine(237)
      .next().atLine(238)
      .next().atLine(239)
      .next().atLine(240)
      .next().atLine(241)
      .next().atLine(242)
      .next().atLine(243)
      .next().atLine(244)
      .next().atLine(245)
      .next().atLine(246)
      .next().atLine(247)
      .next().atLine(248)
      .next().atLine(249)
      .next().atLine(250)
      .next().atLine(251)
      .next().atLine(252)
      .next().atLine(253)
      .next().atLine(254)
      .next().atLine(255)
      .next().atLine(256)
      .next().atLine(257)
      .next().atLine(258)
      .next().atLine(259)
      .next().atLine(260)
      .next().atLine(261)
      .next().atLine(262)
      .next().atLine(263)
      .next().atLine(264)
      .next().atLine(265)
      .next().atLine(268)
      .next().atLine(269)
      .next().atLine(270)
      .next().atLine(271)
      .next().atLine(272)
      .next().atLine(273)
      .next().atLine(274)
      .next().atLine(275)
      .next().atLine(276)
      .next().atLine(277)
      .next().atLine(278)
      .next().atLine(279)
      .next().atLine(280)
      .next().atLine(283)
      .next().atLine(284)
      .next().atLine(285)
      .next().atLine(286)
      .next().atLine(287)
      .next().atLine(288)
      .next().atLine(289)
      .next().atLine(290)
      .next().atLine(291)
      .next().atLine(292)
      .next().atLine(293)
      .next().atLine(294)
      .next().atLine(295)
      .next().atLine(296)
      .next().atLine(297)
      .next().atLine(298)
      .next().atLine(299)
      .next().atLine(300)
      .next().atLine(301)
      .next().atLine(302)
      .next().atLine(303)
      .next().atLine(304)
      .next().atLine(305)
      .next().atLine(306)
      .next().atLine(307)
      .next().atLine(308)
      .next().atLine(309)
      .next().atLine(310)
      .next().atLine(311)
      .next().atLine(312)
      .next().atLine(313)
      .next().atLine(314)
      .next().atLine(315)
      .next().atLine(316)
      .next().atLine(317)
      .next().atLine(318)
      .next().atLine(321)
      .next().atLine(322)
      .next().atLine(323)
      .next().atLine(324)
      .next().atLine(325)
      .next().atLine(326)
      .next().atLine(327)
      .next().atLine(328)
      .noMore();
  }
}
