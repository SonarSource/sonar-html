package org.sonar.plugins.html.checks.accessibility;

import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;

import static org.sonar.plugins.html.api.HtmlConstants.isKnownHTMLTag;
import static org.sonar.plugins.html.api.accessibility.AccessibilityUtils.isFocusableElement;
import static org.sonar.plugins.html.api.accessibility.AccessibilityUtils.isHiddenFromScreenReader;

@Rule(key = "S6825")
public class NoAriaHiddenOnFocusableCheck extends AbstractPageCheck {

  private static final String MESSAGE = "aria-hidden=\"true\" must not be set on focusable elements.";

  @Override
  public void startElement(TagNode node) {
    if (!isKnownHTMLTag(node)) {
      return;
    }
    if (
        isFocusableElement(node) &&
        isHiddenFromScreenReader(node)
    ) {
      createViolation(node, MESSAGE);
    }
  }

}
