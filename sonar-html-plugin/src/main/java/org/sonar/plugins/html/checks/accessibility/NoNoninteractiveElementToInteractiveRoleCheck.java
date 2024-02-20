package org.sonar.plugins.html.checks.accessibility;

import static org.sonar.plugins.html.api.HtmlConstants.INTERACTIVE_ROLES;
import static org.sonar.plugins.html.api.HtmlConstants.KNOWN_HTML_TAGS;
import static org.sonar.plugins.html.api.HtmlConstants.NON_INTERACTIVE_ELEMENTS;

import java.util.Locale;
import java.util.Set;
import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "S6842")
public class NoNoninteractiveElementToInteractiveRoleCheck extends AbstractPageCheck {


  private static final String MESSAGE = "Non-interactive elements should not be assigned interactive roles.";

  @Override
  public void startElement(TagNode node) {
    var role = node.getPropertyValue("role");
    var tagName = node.getNodeName().toLowerCase(Locale.ROOT);
    if (
      role != null &&
      KNOWN_HTML_TAGS.contains(tagName) &&
      NON_INTERACTIVE_ELEMENTS.contains(node.getNodeName().toLowerCase(Locale.ROOT)) &&
      INTERACTIVE_ROLES.contains(role)
    ) {
      createViolation(node, MESSAGE);
    }
  }

}
