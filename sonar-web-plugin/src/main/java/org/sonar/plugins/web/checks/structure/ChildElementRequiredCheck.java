/*
 * SonarWeb :: SonarQube Plugin
 * Copyright (c) 2010-2018 SonarSource SA and Matthijs Galesloot
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
package org.sonar.plugins.web.checks.structure;

import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.node.TagNode;

/**
 * Checker for required child elements.
 *
 * e.g. head must have child element title.
 */
@Rule(key = "ChildElementRequiredCheck")
public class ChildElementRequiredCheck extends AbstractPageCheck {

  private static final String DEFAULT_CHILD = "";
  private static final String DEFAULT_PARENT = "";

  @RuleProperty(
    key = "child",
    description = "Name of the child element",
    defaultValue = DEFAULT_CHILD)
  public String child = DEFAULT_CHILD;

  @RuleProperty(
    key = "parent",
    description = "Name of the parent element",
    defaultValue = DEFAULT_PARENT)
  public String parent = DEFAULT_PARENT;

  @Override
  public void startElement(TagNode element) {
    if (element.equalsElementName(parent)) {
      boolean ruleOK = false;
      for (TagNode childNode : element.getChildren()) {
        if (childNode.equalsElementName(child)) {
          ruleOK = true;
        }
      }
      if (!ruleOK) {
        createViolation(element.getStartLinePosition(), "Add the missing \"" + child + "\" element to this \"" + parent + "\".");
      }
    }
  }

}
