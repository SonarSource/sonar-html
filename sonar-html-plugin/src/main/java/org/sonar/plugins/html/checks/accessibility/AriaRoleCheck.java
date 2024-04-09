package org.sonar.plugins.html.checks.accessibility;

import java.util.Set;

import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "S6821")
public class AriaRoleCheck extends AbstractPageCheck {
    
    @Override
    public void startElement(TagNode element) {
        if (!element.hasProperty("role")) {
            return;
        }
        var roleValue = element.getProperty("role");
        if (roleValue == null) {
            return;
        }
        var values = roleValue.getValue().split(" ");
        var validRoles = Set.of("div");
        for (var value : values) {
            if (!validRoles.contains(value)) {
                createViolation(element, String.format("Elements with ARIA roles must use a valid, non-abstract ARIA role. \"%s\" is not a valid role.", value));
            }
        }
    }
}
