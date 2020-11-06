/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (c) 2010-2020 SonarSource SA and Matthijs Galesloot
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
