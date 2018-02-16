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
package org.sonar.plugins.web.checks.dependencies;

import com.google.common.base.Splitter;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.node.Attribute;
import org.sonar.plugins.web.node.DirectiveNode;
import org.sonar.plugins.web.node.ExpressionNode;
import org.sonar.plugins.web.node.Node;
import org.sonar.squidbridge.annotations.RuleTemplate;

import java.util.List;

@Rule(key = "LibraryDependencyCheck")
@RuleTemplate
public class LibraryDependencyCheck extends AbstractPageCheck {

  private static final String DEFAULT_LIBRARIES = "";
  private static final String DEFAULT_MESSAGE = "Remove the usage of this library which is not allowed.";

  @RuleProperty(
    key = "libraries",
    description = "Comma-separated list of Java packages or classes, such as java.sql or java.util.ArrayList",
    defaultValue = DEFAULT_LIBRARIES)
  public String libraries = DEFAULT_LIBRARIES;

  @RuleProperty(
    key = "message",
    description = "Issue message which is displayed in case of violation",
    defaultValue = "" + DEFAULT_MESSAGE)
  public String message = DEFAULT_MESSAGE;

  private Iterable<String> librariesIterable;

  @Override
  public void startDocument(List<Node> nodes) {
    librariesIterable = Splitter.on(',').trimResults().omitEmptyStrings().split(libraries);
  }

  @Override
  public void directive(DirectiveNode node) {
    if (node.isJsp() && "page".equalsIgnoreCase(node.getNodeName())) {
      for (Attribute attribute : node.getAttributes()) {
        if (isIllegalImport(attribute)) {
          createViolation(node.getStartLinePosition(), message);
        }
      }
    }
  }

  private boolean isIllegalImport(Attribute a) {
    if ("import".equals(a.getName())) {
      for (String library : librariesIterable) {
        if (a.getValue().contains(library)) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public void expression(ExpressionNode node) {
    for (String library : librariesIterable) {
      if (node.getCode().contains(library)) {
        createViolation(node.getStartLinePosition(), message);
      }
    }
  }

}
