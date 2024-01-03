/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (c) 2010-2024 SonarSource SA and Matthijs Galesloot
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
package org.sonar.plugins.html.checks.dependencies;

import java.util.List;
import java.util.regex.Pattern;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Attribute;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "IllegalNamespaceCheck")
public class IllegalNamespaceCheck extends AbstractPageCheck {

  private static final Pattern XMLNS_PREFIX = Pattern.compile("^xmlns", Pattern.CASE_INSENSITIVE);
  private static final String DEFAULT_NAMESPACES = "";

  @RuleProperty(
    key = "namespaces",
    description = "Comma separated list of namespaces",
    defaultValue = DEFAULT_NAMESPACES)
  public String namespaces = DEFAULT_NAMESPACES;

  private String[] namespacesArray;

  @Override
  public void startDocument(List<Node> nodes) {
    namespacesArray = namespaces.isEmpty() ? new String[0] : namespaces.split(",");
  }

  @Override
  public void startElement(TagNode element) {
    for (Attribute a : element.getAttributes()) {
      if (XMLNS_PREFIX.matcher(a.getName()).find()) {
        for (String namespace : namespacesArray) {
          if (a.getValue().equalsIgnoreCase(namespace)) {
            createViolation(element.getStartLinePosition(), "Using \"" + a.getValue() + "\" namespace is not allowed.");
          }
        }
      }
    }
  }

}
