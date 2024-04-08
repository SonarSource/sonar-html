package org.sonar.plugins.html.checks.accessibility;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

public class AriaUnsupportedElementsCheckTest {
    @RegisterExtension
    public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

    @Test
    void html() throws Exception {
        HtmlSourceCode sourceCode = TestHelper.scan(
            new File("src/test/resources/checks/AriaUnsupportedElementsCheck.html"),
            new AriaUnsupportedElementsCheck());
        checkMessagesVerifier.verify(sourceCode.getIssues())
            .next().atLine(1).withMessage("This element does not support ARIA roles, states and properties. Try removing the prop aria-hidden.");
    }
}
