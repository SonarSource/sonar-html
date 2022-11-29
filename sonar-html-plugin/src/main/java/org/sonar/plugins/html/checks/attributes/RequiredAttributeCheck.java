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
package org.sonar.plugins.html.checks.attributes;

import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;

import java.util.ArrayList;
import java.util.List;

@Rule(key = "RequiredAttributeCheck")
public class RequiredAttributeCheck extends AbstractPageCheck {

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
