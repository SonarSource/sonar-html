/*
 * SonarQube HTML Plugin :: Sonar Plugin
 * Copyright (C) 2010-2024 SonarSource SA
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
package org.sonar.plugins.html.checks.coding;

import java.util.List;
import java.util.regex.Pattern;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;
import org.sonar.plugins.html.node.TextNode;

@Rule(key = "InternationalizationCheck")
public class InternationalizationCheck extends AbstractPageCheck {

  private static final String DEFAULT_ATTRIBUTES = "outputLabel.value, outputText.value";

  @RuleProperty(
    key = "attributes",
    description = "Attributes",
    defaultValue = DEFAULT_ATTRIBUTES)
  public String attributes = DEFAULT_ATTRIBUTES;

  @RuleProperty(
    key = "ignoredContentRegex",
    description = "Text content matching this expression will be ignored",
    defaultValue = "")
  public String ignoredContentRegex = "";

  private Pattern ignoredContentPattern = null;
  private QualifiedAttribute[] attributesArray;

  @Override
  public void startDocument(List<Node> nodes) {
    this.attributesArray = parseAttributes(attributes);
    if (!ignoredContentRegex.isEmpty()) {
      ignoredContentPattern = Pattern.compile(ignoredContentRegex);
    }
  }

  @Override
  public void characters(TextNode textNode) {
    String textNodeCode = textNode.getCode();
    if (isValidText(textNodeCode)) {
      createViolation(textNode, "Define this label in the resource bundle.");
    }
  }

  @Override
  public void startElement(TagNode element) {
    for (QualifiedAttribute attribute : attributesArray) {
      if (notValid(element, attribute)) {
        return;
      }
    }
  }

  private boolean notValid(TagNode element, QualifiedAttribute attribute) {
    if (element.equalsElementName(attribute.getNodeName())) {
      String value = element.getAttribute(attribute.getAttributeName());
      if (value != null) {
        value = value.trim();
        if (value.length() > 0 && isValidText(value)) {
          createViolation(element, "Define this label in the resource bundle.");
          return true;
        }
      }
    }
    return false;
  }

  private boolean isValidText(String value) {
    return !isUnifiedExpression(value) && hasNoPunctuationOrSpace(value) && !isIgnoredByRegex(value);
  }

  private static boolean hasNoPunctuationOrSpace(String value) {
    return value.chars().allMatch(Character::isLetterOrDigit);
  }

  private boolean isIgnoredByRegex(String value) {
    return ignoredContentPattern != null && ignoredContentPattern.matcher(value).matches();
  }
}
