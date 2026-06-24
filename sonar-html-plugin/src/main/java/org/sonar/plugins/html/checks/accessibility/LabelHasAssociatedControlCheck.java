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
package org.sonar.plugins.html.checks.accessibility;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import org.sonar.check.Rule;
import org.sonar.plugins.html.api.Helpers;
import org.sonar.plugins.html.api.Thymeleaf;
import org.sonar.plugins.html.api.accessibility.AccessibilityUtils;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Attribute;
import org.sonar.plugins.html.node.DirectiveNode;
import org.sonar.plugins.html.node.ExpressionNode;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;
import org.sonar.plugins.html.node.TextNode;

@Rule(key = "S6853")
public class LabelHasAssociatedControlCheck extends AbstractPageCheck {
  private static final String MESSAGE = "A form label must be associated with a control and have accessible text.";
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
  private boolean foundLabelBodyContent;
  private TagNode label;

  @Override
  public void startDocument(List<Node> nodes) {
    foundControl = false;
    foundAccessibleLabel = false;
    foundLabelBodyContent = false;
    label = null;
  }

  @Override
  public void startElement(TagNode node) {
    if (isLabel(node)) {
      label = node;
      foundControl = hasControlAssociationHint(label);
      foundAccessibleLabel = hasAccessibleTextHint(label);
      foundLabelBodyContent = false;
      // A fragment-rendered label is opaque to static analysis — accept both axes.
      if (Thymeleaf.hasFragmentInsertion(label)) {
        foundControl = true;
        foundAccessibleLabel = true;
      }
    } else {
      if (label != null) {
        foundLabelBodyContent = true;
      }
      if (isControl(node)) {
        foundControl = true;
      }
      if (label != null && hasAccessibleTextHint(node)) {
        foundAccessibleLabel = true;
      }
      // Razor view-component or <partial> child can supply both text and control.
      if (label != null && Helpers.isRazorFile(getHtmlSourceCode()) && Helpers.isRazorFragmentTagHelper(node)) {
        foundControl = true;
        foundAccessibleLabel = true;
      }
    }
  }

  private static boolean hasControlAssociationHint(TagNode label) {
    return hasPropertyHint(label, "for")
      || hasPropertyHint(label, "htmlFor")
      || hasAttributeHint(label, "asp-for")
      || Thymeleaf.hasNonEmptyThymeleafAttribute(label, "for");
  }

  private static boolean hasAccessibleTextHint(TagNode node) {
    return hasPropertyHint(node, "alt")
      || hasPropertyHint(node, "aria-labelledby")
      || hasPropertyHint(node, "aria-label")
      // Angular [innerText]/[innerHTML]/[textContent] write text content at runtime.
      || hasPropertyHint(node, "innerText")
      || hasPropertyHint(node, "innerHTML")
      || hasPropertyHint(node, "textContent")
      // Thymeleaf th:aria-label / th:attr="aria-label=..." (and aria-labelledby/alt variants).
      || Thymeleaf.hasNonEmptyThymeleafAttribute(node, "aria-label")
      || Thymeleaf.hasNonEmptyThymeleafAttribute(node, "aria-labelledby")
      || Thymeleaf.hasNonEmptyThymeleafAttribute(node, "alt")
      || AccessibilityUtils.hasNonEmptyTemplateTextAttribute(node)
      // see https://sonarsource.github.io/rspec/#/rspec/S1926
      || "FMT:MESSAGE".equalsIgnoreCase(node.getNodeName());
  }

  // Property lookup that accepts Angular/Vue binding forms even with empty value — the binding name itself is the hint.
  private static boolean hasPropertyHint(TagNode node, String propertyName) {
    Attribute property = node.getProperty(propertyName);
    return property != null && (isBindingForm(property, propertyName) || !Thymeleaf.isEmptyValue(property.getValue()));
  }

  private static boolean hasAttributeHint(TagNode node, String attributeName) {
    return !Thymeleaf.isEmptyValue(node.getAttribute(attributeName));
  }

  private static boolean isBindingForm(Attribute attribute, String canonicalName) {
    return !canonicalName.equalsIgnoreCase(attribute.getName());
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
        foundLabelBodyContent = true;
        foundAccessibleLabel = true;
      }
      // Check for Razor HTML helpers that render form controls (e.g., @Html.RadioButtonFor)
      if (RAZOR_CONTROL_PATTERN.matcher(textNode.getCode()).find()) {
        foundControl = true;
      }
      // Razor fragment rendering (@Html.PartialAsync, @RenderBody, ...) is opaque.
      if (Helpers.isRazorFile(getHtmlSourceCode()) && Helpers.containsRazorFragmentRendering(textNode.getCode())) {
        foundControl = true;
        foundAccessibleLabel = true;
      }
    }
  }

  @Override
  public void directive(DirectiveNode node) {
    if (label != null) {
      foundLabelBodyContent = true;
      foundAccessibleLabel = true;
    }
  }

  @Override
  public void expression(ExpressionNode node) {
    // for JSP
    if (label != null) {
      foundLabelBodyContent = true;
      foundAccessibleLabel = true;
    }
  }

  @Override
  public void endElement(TagNode node) {
    if (isLabel(node)) {
      if (label != null && hasAttributeHint(label, "asp-for") && !foundLabelBodyContent) {
        foundAccessibleLabel = true;
      }
      if ((!foundAccessibleLabel || !foundControl) && label != null) {
        createViolation(label, MESSAGE);
      }
      foundControl = false;
      foundAccessibleLabel = false;
      foundLabelBodyContent = false;
      label = null;
    }
  }
}
