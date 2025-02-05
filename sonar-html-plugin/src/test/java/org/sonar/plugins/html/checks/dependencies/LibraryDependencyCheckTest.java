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
package org.sonar.plugins.html.checks.dependencies;

import java.io.File;
import java.io.FileNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

import static org.assertj.core.api.Assertions.assertThat;

public class LibraryDependencyCheckTest {

  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  public void default_parameters() {
    LibraryDependencyCheck check = new LibraryDependencyCheck();
    assertThat(check.libraries).isEmpty();
    assertThat(check.message).isEqualTo("Remove the usage of this library which is not allowed.");
  }

  @Test
  public void illegal_fully_qualified_identifier() {
    LibraryDependencyCheck check = new LibraryDependencyCheck();
    check.libraries = "java.sql";

    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/LibraryDependencyCheck/IllegalFullyQualifiedIdentifier.jsp"), check);

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(1).withMessage("Remove the usage of this library which is not allowed.");
  }

  @Test
  public void illegal_fully_qualified_identifier_with_custom_message() {
    LibraryDependencyCheck check = new LibraryDependencyCheck();
    check.libraries = "java.sql";
    check.message = "Foo.";

    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/LibraryDependencyCheck/IllegalFullyQualifiedIdentifier.jsp"), check);

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(1).withMessage("Foo.");
  }

  @Test
  public void illegal_import() throws FileNotFoundException {
    LibraryDependencyCheck check = new LibraryDependencyCheck();
    check.libraries = "java.sql";

    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/LibraryDependencyCheck/IllegalImport.jsp"), check);

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(2).withMessage("Remove the usage of this library which is not allowed.");
  }

  @Test
  public void legal_fully_qualified_identifier_and_import() throws FileNotFoundException {
    LibraryDependencyCheck check = new LibraryDependencyCheck();
    check.libraries = "java.sql";

    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/LibraryDependencyCheck/LegalFullyQualifiedIdentifierAndImport.jsp"), check);

    checkMessagesVerifier.verify(sourceCode.getIssues());
  }

  @Test
  public void html_page() throws FileNotFoundException {
    LibraryDependencyCheck check = new LibraryDependencyCheck();
    check.libraries = "java.sql";

    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/LibraryDependencyCheck/HtmlPage.html"), check);

    checkMessagesVerifier.verify(sourceCode.getIssues());
  }

}
