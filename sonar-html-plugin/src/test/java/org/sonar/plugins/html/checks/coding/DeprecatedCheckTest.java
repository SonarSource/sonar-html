package org.sonar.plugins.html.checks.coding;

import java.io.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

class DeprecatedCheckTest {

  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  void detected() {
    HtmlSourceCode sourceCode = TestHelper.scan(
            new File("src/test/resources/checks/DeprecatedCheck.html"), new DeprecatedCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
            .next().atLine(7).withMessage("The <center> tag is deprecated/obsolete and should not be used.")
            .next().atLine(8).withMessage("The <font> tag is deprecated/obsolete and should not be used.")
            .next().atLine(9).withMessage("The <marquee> tag is deprecated/obsolete and should not be used.")
            .next().atLine(10).withMessage("The 'border' attribute on <table> is deprecated/obsolete and should not be used.")
            .next().atLine(11).withMessage("The 'align' attribute on <tr> is deprecated/obsolete and should not be used.")
            .next().atLine(12).withMessage("The 'bgcolor' attribute on <td> is deprecated/obsolete and should not be used.");
  }

}