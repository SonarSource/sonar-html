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

import com.google.common.base.Splitter;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.node.Attribute;
import org.sonar.plugins.web.node.DirectiveNode;
import org.sonar.plugins.web.node.ExpressionNode;
import org.sonar.plugins.web.node.Node;

import java.util.List;

@Rule(key = "LibraryDependencyCheck", priority = Priority.CRITICAL)
public class LibraryDependencyCheck extends AbstractPageCheck {

  private static final String DEFAULT_LIBRARIES = "";

  @RuleProperty(
    key = "libraries",
    defaultValue = DEFAULT_LIBRARIES)
  public String libraries = DEFAULT_LIBRARIES;

  private Iterable<String> librariesIterable;

  @Override
  public void startDocument(List<Node> nodes) {
    librariesIterable = Splitter.on(',').trimResults().omitEmptyStrings().split(libraries);
  }

  @Override
  public void directive(DirectiveNode node) {
    if (libraries != null && node.isJsp() && "page".equalsIgnoreCase(node.getNodeName())) {
      for (Attribute a : node.getAttributes()) {
        String illegalLibrary = getIllegalImport(a);
        if (illegalLibrary != null) {
          createViolation(node.getStartLinePosition(), "Using '" + illegalLibrary + "' library is not allowed.");
        }
      }
    }
  }

  @Override
  public void expression(ExpressionNode node) {
    for (String library : librariesIterable) {
      if (node.getCode().contains(library)) {
        createViolation(node.getStartLinePosition(), "Using '" + library + "' library is not allowed.");
      }
    }
  }

  private String getIllegalImport(Attribute a) {
    if ("import".equals(a.getName())) {
      for (String library : librariesIterable) {
        if (a.getValue().contains(library)) {
          return library;
        }
      }
    }
    return null;
  }

}
