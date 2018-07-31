/*
 * SonarHTML :: SonarQube Plugin
 * Copyright (c) 2010-2018 SonarSource SA and Matthijs Galesloot
 * sonarqube@googlegroups.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sonar.plugins.html.checks.dependencies;

import static org.fest.assertions.Assertions.assertThat;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.Rule;
import org.junit.Test;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

public class LibraryDependencyCheckTest {

  @Rule
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
