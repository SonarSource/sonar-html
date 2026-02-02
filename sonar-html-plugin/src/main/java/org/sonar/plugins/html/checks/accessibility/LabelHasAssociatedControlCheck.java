/*
 * SonarQube HTML
 * Copyright (C) 2010-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package org.sonar.plugins.html.checks.accessibility;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
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
  private static final Set<String> CONTROL_TAGS = Set.of("INPUT", "METER", "OUTPUT", "PROGRESS", "SELECT", "TEXTAREA");

  // Pattern to detect Razor HTML helpers that render form control elements.
  // These helpers (e.g., @Html.TextBoxFor, @Html.RadioButtonFor) generate <input>, <select>, or <textarea> at runtime.
  // Since Razor syntax is not parsed as ExpressionNode (only JSP expressions are), we detect these patterns in text content.
  // See: https://community.sonarsource.com/t/web-s6836-not-recognising-contained-elements/176949
  private static final Pattern RAZOR_CONTROL_PATTERN = Pattern.compile(
    "@Html\\.(TextBox|TextBoxFor|TextArea|TextAreaFor|CheckBox|CheckBoxFor|RadioButton|RadioButtonFor|" +
    "DropDownList|DropDownListFor|ListBox|ListBoxFor|Password|PasswordFor|Hidden|HiddenFor|Editor|EditorFor)\\b",
    Pattern.CASE_INSENSITIVE);
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
      if (hasForAttribute(label)) {
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

  private static boolean hasForAttribute(TagNode label) {
    return label.hasProperty("for") ||
           label.hasProperty("htmlFor") ||
           label.hasProperty("[for]") ||        // Angular binding
           label.hasProperty(":for") ||         // Vue shorthand binding
           label.hasProperty("v-bind:for");     // Vue full binding syntax
  }

  private static boolean hasAccessibleLabel(TagNode node) {
    return
      node.hasProperty("alt") ||
      node.hasProperty("aria-labelledby") ||
      node.hasProperty("aria-label") ||
      // see https://sonarsource.github.io/rspec/#/rspec/S1926
      "FMT:MESSAGE".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isLabel(TagNode node) {
    return "LABEL".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isControl(TagNode node) {
    return CONTROL_TAGS.contains(node.getNodeName().toUpperCase(Locale.ROOT));
  }

  @Override
  public void characters(TextNode textNode) {
    if (label != null) {
      if (!textNode.isBlank()) {
        foundAccessibleLabel = true;
      }
      // Check for Razor HTML helpers that render form controls (e.g., @Html.RadioButtonFor)
      if (RAZOR_CONTROL_PATTERN.matcher(textNode.getCode()).find()) {
        foundControl = true;
      }
    }
  }

  @Override
  public void directive(DirectiveNode node) {
    if (label != null) {
      foundAccessibleLabel = true;
    }
  }

  @Override
  public void expression(ExpressionNode node) {
    // for JSP
    if (label != null) {
      foundAccessibleLabel = true;
    }
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
}
