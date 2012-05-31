/*
 * Sonar Web Plugin
 * Copyright (C) 2010 Matthijs Galesloot
 * dev@sonar.codehaus.org
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
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.node.Attribute;
import org.sonar.plugins.web.node.TagNode;

/**
 * Checker for occurrence of disallowed namespaces.
 *
 * Checks the namespaces in the root element of the XML document. A list of illegal namespaces can be configured as a property of the check.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
@Rule(key = "IllegalNamespaceCheck", priority = Priority.MAJOR)
public class IllegalNamespaceCheck extends AbstractPageCheck {

  @RuleProperty
  private String[] namespaces;
  private boolean visited;

  public String getNamespaces() {
    if (namespaces != null) {
      return StringUtils.join(namespaces, ",");
    }
    return "";
  }

  public void setNamespaces(String list) {
    namespaces = StringUtils.split(list, ",");
  }

  @Override
  public void startElement(TagNode element) {

    if (visited || namespaces == null) {
      return;
    }

    for (Attribute a : element.getAttributes()) {

      if (StringUtils.startsWithIgnoreCase(a.getName(), "xmlns")) {
        for (String namespace : namespaces) {
          if (a.getValue().equalsIgnoreCase(namespace)) {
            createViolation(element);
          }
        }
      }
    }
  }
}
