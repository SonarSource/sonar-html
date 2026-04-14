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
package org.sonar.plugins.html.checks.attributes;

import java.util.ArrayList;
import java.util.List;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;

public abstract class AbstractRequiredAttributeCheck extends AbstractPageCheck {

  private static final String DEFAULT_ATTRIBUTES = "";

  @RuleProperty(
    key = "attributes",
    description = "Comma-separated list of tag.attributes that are required. E.G. specify img.alt to require an \"alt\" attribute in an \"img\" tag.",
    defaultValue = DEFAULT_ATTRIBUTES)
  public String attributes = DEFAULT_ATTRIBUTES;

  private final List<RequiredAttribute> attributesList = new ArrayList<>();

  private static final class RequiredAttribute {
    private String elementName;
    private String attributeName;
  }

  @Override
  public void startDocument(List<Node> nodes) {
    attributesList.clear();
    for (String item : trimSplitCommaSeparatedList(attributes)) {
      String[] pair = item.split("\\.");
      if (pair.length > 1) {
        RequiredAttribute a = new RequiredAttribute();
        a.elementName = pair[0];
        a.attributeName = pair[1];
        attributesList.add(a);
      }
    }
  }

  @Override
  public void startElement(TagNode node) {
    for (RequiredAttribute attribute : attributesList) {
      String attributeName = attribute.attributeName;
      String elementName = attribute.elementName;
      if (node.equalsElementName(elementName) && !node.hasProperty(attributeName)) {
        createViolation(node.getStartLinePosition(), "Add the missing \"" + attributeName + "\" to element \"" + elementName + "\".");
      }
    }
  }

}
