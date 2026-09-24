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

class ImgWithoutAltCheckTest {
  private static final String IMG_MESSAGE = "This <img> lacks an accessible name; add an \"alt\" attribute (or \"aria-label\"/\"aria-labelledby\")."
    + " Alternatively, mark it as decorative (e.g. aria-hidden or role=\"presentation\").";
  private static final String AREA_MESSAGE = "This <area> lacks an accessible name; add an \"alt\" attribute (or \"aria-label\"/\"aria-labelledby\").";
  private static final String INPUT_IMAGE_MESSAGE = "This <input type=\"image\"> lacks an accessible name; add an \"alt\" attribute (or \"aria-label\"/\"aria-labelledby\").";
  private static final String SVG_MESSAGE = "This <svg> lacks an accessible name; add a \"title\" child, \"aria-label\", or \"aria-labelledby\"."
    + " Alternatively, mark it as decorative (e.g. aria-hidden or role=\"presentation\").";

  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  void detected() {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/ImgWithoutAltCheck.html"), new ImgWithoutAltCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLocation(1, 0, 1, 7).withMessage(IMG_MESSAGE)
        .next().atLine(5).withMessage(INPUT_IMAGE_MESSAGE)
        .next().atLine(6)
        .next().atLine(7)
        .next().atLine(14)
        .next().atLine(15)
        .next().atLine(16)
        .next().atLine(17)
        .next().atLine(19).withMessage(AREA_MESSAGE)
        .next().atLine(20)
        .next().atLine(21)
        .next().atLine(30);
  }

  @Test
  void supportsAriaAlternativeText() {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/ImgWithoutAltCheckAria.html"), new ImgWithoutAltCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(3).withMessage(IMG_MESSAGE)
      .next().atLine(4)
      .next().atLine(7).withMessage(INPUT_IMAGE_MESSAGE)
      .next().atLine(8)
      .next().atLine(11).withMessage(AREA_MESSAGE)
      .next().atLine(12);
  }

  @Test
  void detectsSvgWithoutAccessibleName() {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/ImgWithoutAltCheckSvg.html"), new ImgWithoutAltCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(1).withMessage(SVG_MESSAGE)
      .next().atLine(3)
      .next().atLine(5)
      .next().atLine(13)
      .next().atLine(14)
      .next().atLine(15)
      .next().atLine(16)
      .next().atLine(23)
      .next().atLine(28)
      .noMore();
  }

  @Test
  void exemptsDecorativeImg() {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/ImgWithoutAltCheckDecorative.html"), new ImgWithoutAltCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(1).withMessage(IMG_MESSAGE)
      .next().atLine(4)
      .noMore();
  }

  @Test
  void rejectsEmptyThymeleafAlternativeText() {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/ImgWithoutAltCheckThymeleaf.html"), new ImgWithoutAltCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(5).withMessage(IMG_MESSAGE)
      .next().atLine(6)
      .next().atLine(8).withMessage(INPUT_IMAGE_MESSAGE)
      .next().atLine(10)
      .next().atLine(12).withMessage(AREA_MESSAGE)
      .next().atLine(14)
      .next().atLine(20)
      .next().atLine(21)
      .next().atLine(22)
      .next().atLine(23)
      .next().atLine(24)
      .next().atLine(25)
      .next().atLine(27);
  }
}
