/*
 * Sonar Web Plugin
 * Copyright (C) 2010 SonarSource and Matthijs Galesloot
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

import com.google.common.base.Splitter;
import org.sonar.check.Cardinality;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.node.Attribute;
import org.sonar.plugins.web.node.DirectiveNode;
import org.sonar.plugins.web.node.ExpressionNode;
import org.sonar.plugins.web.node.Node;

import java.util.List;

@Rule(
  key = "LibraryDependencyCheck",
  priority = Priority.MAJOR,
  cardinality = Cardinality.MULTIPLE)
public class LibraryDependencyCheck extends AbstractPageCheck {

  private static final String DEFAULT_LIBRARIES = "";
  private static final String DEFAULT_MESSAGE = "Remove the usage of this library which is not allowed.";

  @RuleProperty(
    key = "libraries",
    defaultValue = DEFAULT_LIBRARIES)
  public String libraries = DEFAULT_LIBRARIES;

  @RuleProperty(
    key = "message",
    defaultValue = "" + DEFAULT_MESSAGE)
  public String message = DEFAULT_MESSAGE;

  private Iterable<String> librariesIterable;

  @Override
  public void startDocument(List<Node> nodes) {
    System.out.println("got libs: " + libraries);
    librariesIterable = Splitter.on(',').trimResults().omitEmptyStrings().split(libraries);
    System.out.println("iterable:");
    for (String lib : librariesIterable) {
      System.out.println(" ---> " + lib);
    }
    System.out.println("end iterable!");
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
