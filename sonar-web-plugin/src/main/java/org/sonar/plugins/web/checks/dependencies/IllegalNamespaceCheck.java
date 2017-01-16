/*
 * SonarSource :: Web :: Sonar Plugin
 * Copyright (c) 2010-2017 SonarSource SA and Matthijs Galesloot
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
package org.sonar.plugins.web.checks.dependencies;

import org.apache.commons.lang.StringUtils;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.node.Attribute;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.node.TagNode;

import java.util.List;

@Rule(key = "IllegalNamespaceCheck")
public class IllegalNamespaceCheck extends AbstractPageCheck {

  private static final String DEFAULT_NAMESPACES = "";

  @RuleProperty(
    key = "namespaces",
    description = "Comma separated list of namespaces",
    defaultValue = DEFAULT_NAMESPACES)
  public String namespaces = DEFAULT_NAMESPACES;

  private String[] namespacesArray;

  @Override
  public void startDocument(List<Node> nodes) {
    namespacesArray = StringUtils.split(namespaces, ",");
  }

  @Override
  public void startElement(TagNode element) {
    for (Attribute a : element.getAttributes()) {
      if (StringUtils.startsWithIgnoreCase(a.getName(), "xmlns")) {
        for (String namespace : namespacesArray) {
          if (a.getValue().equalsIgnoreCase(namespace)) {
            createViolation(element.getStartLinePosition(), "Using \"" + a.getValue() + "\" namespace is not allowed.");
          }
        }
      }
    }
  }

}
