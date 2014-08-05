/*
 * SonarQube Web Plugin
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

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.checks.WebRule;
import org.sonar.plugins.web.node.Attribute;
import org.sonar.plugins.web.node.DirectiveNode;
import org.sonar.plugins.web.node.Node;

import java.util.List;

/**
 * Checker to find disallowed taglibs.
 *
 * e.g. <%@ taglib prefix="sql" uri="http://java.sun.com/jstl/sql" %>
 */
@Rule(
  key = "IllegalTagLibsCheck",
  priority = Priority.CRITICAL)
@WebRule(activeByDefault = false)
public class IllegalTagLibsCheck extends AbstractPageCheck {

  private static final String DEFAULT_TAG_LIBS = "http://java.sun.com/jstl/sql";

  @RuleProperty(
    key = "tagLibs",
    defaultValue = DEFAULT_TAG_LIBS)
  public String tagLibs = DEFAULT_TAG_LIBS;

  private String[] tagLibsArray;

  @Override
  public void startDocument(List<Node> nodes) {
    tagLibsArray = trimSplitCommaSeparatedList(tagLibs);
  }

  @Override
  public void directive(DirectiveNode node) {
    if ("taglib".equalsIgnoreCase(node.getNodeName())) {
      for (Attribute a : node.getAttributes()) {
        for (String tagLib : tagLibsArray) {
          if (tagLib.equalsIgnoreCase(a.getValue())) {
            createViolation(node.getStartLinePosition(), "Following taglib is forbidden: " + tagLib);
          }
        }
      }
    }
  }

}
