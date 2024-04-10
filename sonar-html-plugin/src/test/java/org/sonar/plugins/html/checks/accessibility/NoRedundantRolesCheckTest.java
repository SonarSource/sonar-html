package org.sonar.plugins.html.checks.accessibility;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

class NoRedundantRolesCheckTest {
  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  void html() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(
        new File("src/test/resources/checks/NoRedundantRolesCheck.html"),
        new NoRedundantRolesCheck());
    System.out.println("sourceCode: " + sourceCode.getIssues());
    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLine(1)
        .withMessage(
            "The element button has an implicit role of button. Definig this explicitly is redundant and should be avoided.")
        .next().atLine(2)
        .noMore();
  }
}
