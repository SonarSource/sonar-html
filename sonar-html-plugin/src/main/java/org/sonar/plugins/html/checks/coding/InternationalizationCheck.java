/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (c) 2010-2022 SonarSource SA and Matthijs Galesloot
 * sonarqube@googlegroups.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
