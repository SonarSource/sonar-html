package org.sonar.plugins.html.checks.accessibility;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.DirectiveNode;
import org.sonar.plugins.html.node.ExpressionNode;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;
import org.sonar.plugins.html.node.TextNode;

@Rule(key = "S6853")
public class LabelHasAssociatedControlCheck extends AbstractPageCheck {
  private static final String MESSAGE = "A form label must be associated with a control.";
  private static final String[] CONTROL_TAGS = {"input", "meter", "output", "progress", "select", "textarea"};
  private boolean foundControl;
  private boolean foundAccessibleLabel;
  private TagNode label;

  @Override
  public void startDocument(List<Node> nodes) {
    label = null;
  }

  @Override
  public void startElement(TagNode node) {
    if (isLabel(node)) {
      label = node;
      if (label.hasProperty("for")) {
        foundControl = true;
      } else {
        foundControl = false;
      }
    } else if (isControl(node)) {
      foundControl = true;
    }
    if (hasAccessibleLabel(node)) {
      foundAccessibleLabel = true;
    }
  }

  @Override
  public void characters(TextNode textNode) {
    if (!textNode.isBlank()) {
      foundAccessibleLabel = true;
    }
  }

  private boolean hasAccessibleLabel(TagNode node) {
    return
      node.hasProperty("alt") ||
      node.hasProperty("aria-labelledby") ||
      node.hasProperty("aria-label");
  }

  @Override
  public void endElement(TagNode node) {
    if (isLabel(node)) {
      if ((!foundAccessibleLabel || !foundControl) && label != null) {
        createViolation(label, MESSAGE);
      }
      foundControl = false;
      foundAccessibleLabel = false;
      label = null;
    }
  }

  private boolean isLabel(TagNode node) {
    return "LABEL".equalsIgnoreCase(node.getNodeName());
  }

  private boolean isControl(TagNode node) {
    return Arrays.stream(CONTROL_TAGS).anyMatch(node.getNodeName()::equalsIgnoreCase);
  }
}
