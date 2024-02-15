package org.sonar.plugins.html.checks.accessibility;

import java.io.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

public class LabelHasAssociatedControlCheckTest {
  @RegisterExtension
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  void nesting() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(
            new File("src/test/resources/checks/LabelHasAssociatedControlCheck/nesting.html"),
            new LabelHasAssociatedControlCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
            .next().atLine(2)
            .next().atLine(3)
            .next().atLine(5)
            .next().atLine(10)
            .noMore();
  }

  @Test
  void forAttribute() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(
            new File("src/test/resources/checks/LabelHasAssociatedControlCheck/" +
                    "for.html"),
            new LabelHasAssociatedControlCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
            .next().atLine(2)
            .next().atLine(3)
            .next().atLine(6)
            .next().atLine(8)
            .next().atLine(10)
            .next().atLine(12)
            .noMore();
  }
}
