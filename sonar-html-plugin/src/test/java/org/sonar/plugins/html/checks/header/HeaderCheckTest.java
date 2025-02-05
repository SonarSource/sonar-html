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
package org.sonar.plugins.html.checks.header;

import java.io.File;
import java.io.FileReader;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.sonar.plugins.html.api.HtmlConstants;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.lex.PageLexer;
import org.sonar.plugins.html.visitor.DefaultNodeVisitor;
import org.sonar.plugins.html.visitor.HtmlAstScanner;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HeaderCheckTest {


  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  public void correct_header() {
    HeaderCheck check = new HeaderCheck();
    check.headerFormat = "<!-- Copyright foo -->";

    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/HeaderCheck/CorrectHeader.html"), check);

    checkMessagesVerifier.verify(sourceCode.getIssues());
  }

  @Test
  public void missing_header() {
    HeaderCheck check = new HeaderCheck();
    check.headerFormat = "<!-- Copyright foo -->";

    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/HeaderCheck/MissingHeader.html"), check);

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(null).withMessage("Add or update the header of this file.");
  }

  @Test
  public void misspelled_header() {
    HeaderCheck check = new HeaderCheck();
    check.headerFormat = "<!-- Copyright foo -->";

    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/HeaderCheck/MisspelledHeader.html"), check);

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(null).withMessage("Add or update the header of this file.");
  }

  @Test
  public void regex1() {
    HeaderCheck check = new HeaderCheck();
    check.headerFormat = "<!-- copyright \\d{4}\\n  mycompany -->";
    check.isRegularExpression = true;

    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/HeaderCheck/Regex1.html"), check);

    checkMessagesVerifier.verify(sourceCode.getIssues()).noMore();
  }

  @Test
  public void regex2() {
    HeaderCheck check = new HeaderCheck();
    check.headerFormat = "<!-- copyright \\d{4}\\n  mycompany -->";
    check.isRegularExpression = true;

    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/HeaderCheck/Regex2.html"), check);

    checkMessagesVerifier.verify(sourceCode.getIssues())
      .next().atLine(null).withMessage("Add or update the header of this file.");
  }

  @Test
  public void should_fail_with_bad_regular_expression() {
    HeaderCheck check = new HeaderCheck();
    check.headerFormat = "*";
    check.isRegularExpression = true;

    var e = assertThrows(IllegalArgumentException.class, () -> check.init());
    assertEquals("[" + HeaderCheck.class.getSimpleName() + "] Unable to compile the regular expression: *", e.getMessage());

  }

  @Test
  public void should_fail_if_unable_to_read_file_without_regex() throws Exception {
    HeaderCheck check = new HeaderCheck();
    check.headerFormat = "<!-- Copyright foo -->";

    assertThrows(IllegalStateException.class,
      () -> scanWithWrongInputFile(new File("src/test/resources/checks/HeaderCheck/CorrectHeader.html"), check));
  }

  @Test
  public void should_fail_if_unable_to_read_file_with_regex() throws Exception {
    HeaderCheck check = new HeaderCheck();
    check.headerFormat = "<!-- copyright \\\\d{4}\\\\n  mycompany -->";
    check.isRegularExpression = true;

      assertThrows(IllegalStateException.class,
        () -> scanWithWrongInputFile(new File("src/test/resources/checks/HeaderCheck/CorrectHeader.html"), check));
  }

  public static void scanWithWrongInputFile(File file, DefaultNodeVisitor visitor) {
    HtmlAstScanner walker = new HtmlAstScanner(Collections.emptyList());
    walker.addVisitor(visitor);
    FileReader reader;
    try {
      reader = new FileReader(file);
    } catch (Exception e) {
      throw new IllegalArgumentException("unable to read file");
    }

    HtmlSourceCode result = new HtmlSourceCode(
      new TestInputFileBuilder("key", /* wrong path */ ".")
        .setLanguage(HtmlConstants.LANGUAGE_KEY)
        .setType(InputFile.Type.MAIN)
        .setModuleBaseDir(new File(".").toPath())
        .build()
    );

    walker.scan(
      new PageLexer().parse(reader),
      // won't be able to resolve the file
      result
    );
  }

}
