/*
 * SonarQube HTML
 * Copyright (C) 2010-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource Sàrl.
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

import java.util.List;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Attribute;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "IllegalAttributeCheck")
public class IllegalAttributeCheck extends AbstractPageCheck {

  private static final String DEFAULT_ATTRIBUTES = "";

  @RuleProperty(
    key = "attributes",
    description = "Comma-separated list of tag.attributes that are not allowed. E.G. a.name forbids a \"name\" attribute in an \"a\" tag.",
    defaultValue = DEFAULT_ATTRIBUTES)
  public String attributes = DEFAULT_ATTRIBUTES;

  private QualifiedAttribute[] attributesArray;

  @Override
  public void startDocument(List<Node> nodes) {
    this.attributesArray = parseAttributes(attributes);
  }

  @Override
  public void startElement(TagNode element) {
    for (Attribute a : getMatchingAttributes(element, attributesArray)) {
      createViolation(element.getStartLinePosition(), "Remove the \"" + a.getName() + "\" attribute from the \"" + element.getNodeName() + "\" tag");
    }
  }

}
