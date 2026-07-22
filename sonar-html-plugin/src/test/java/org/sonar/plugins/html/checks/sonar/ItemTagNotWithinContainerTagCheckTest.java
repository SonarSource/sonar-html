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

class ItemTagNotWithinContainerTagCheckTest {

  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  void detected() {
    HtmlSourceCode sourceCode = TestHelper.scan(
        new File("src/test/resources/checks/ItemTagNotWithinContainerTagCheck/invalid-cases.html"),
        new ItemTagNotWithinContainerTagCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLocation(1, 0, 1, 4).withMessage("Surround this <li> item tag by a <ul> or <ol> container one.")
        .next().atLocation(4, 0, 4, 4).withMessage("Surround this <DT> item tag by a <dl> container one.")
        .next().atLine(8).withMessage("Surround this <li> item tag by a <ul> or <ol> container one.")
        .next().atLine(12).withMessage("Surround this <dt> item tag by a <dl> container one.")
        .next().atLine(17).withMessage("Surround this <li> item tag by a <ul> or <ol> container one.")
        .next().atLine(23).withMessage("Surround this <dt> item tag by a <dl> container one.")
        .next().atLine(29).withMessage("Surround this <li> item tag by a <ul> or <ol> container one.")
        .next().atLine(35).withMessage("Surround this <dt> item tag by a <dl> container one.");
  }

  @Test
  void compliant() {
    HtmlSourceCode sourceCode = TestHelper.scan(
        new File("src/test/resources/checks/ItemTagNotWithinContainerTagCheck/valid-cases.html"),
        new ItemTagNotWithinContainerTagCheck());
    HtmlSourceCode vueSourceCode = TestHelper.scan(
        new File("src/test/resources/checks/ItemTagNotWithinContainerTagCheck/custom-component.vue"),
        new ItemTagNotWithinContainerTagCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues());
    checkMessagesVerifier.verify(vueSourceCode.getIssues());
  }

  @Test
  void razorSection() {
    HtmlSourceCode sourceCode = TestHelper.scan(
        new File("src/test/resources/checks/ItemTagNotWithinContainerTagCheck/razor-section.cshtml"),
        new ItemTagNotWithinContainerTagCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(15).withMessage("Surround this <li> item tag by a <ul> or <ol> container one.")
        .next().atLine(17).withMessage("Surround this <dt> item tag by a <dl> container one.")
        .next().atLine(20).withMessage("Surround this <li> item tag by a <ul> or <ol> container one.")
        .next().atLine(24).withMessage("Surround this <li> item tag by a <ul> or <ol> container one.");
  }

}
