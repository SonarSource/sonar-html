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
import org.sonar.check.IsoCategory;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.checks.Utils;
import org.sonar.plugins.web.node.Attribute;
import org.sonar.plugins.web.node.DirectiveNode;
import org.sonar.plugins.web.node.ExpressionNode;

/**
 * Checker to find libraries that should not be used.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
@Rule(key = "LibraryDependencyCheck", name = "Library Dependency Check", description = "Dependencies to certain libraries are not allowed",
    priority = Priority.CRITICAL, isoCategory = IsoCategory.Maintainability)
public class LibraryDependencyCheck extends AbstractPageCheck {

  @RuleProperty(key = "libraries", description = "Libraries")
  private String[] libraries;

  public String getLibraries() {
    if (libraries != null) {
      return StringUtils.join(libraries, ",");
    }
    return "";
  }

  public void setLibraries(String list) {
    libraries = Utils.trimSplitCommaSeparatedList(list);
  }

  @Override
  public void directive(DirectiveNode node) {
    if (libraries != null && node.isJsp() && "page".equalsIgnoreCase(node.getNodeName())) {
      for (Attribute a : node.getAttributes()) {
        if (isIllegalImport(a)) {
          createViolation(node);
        }
      }
    }
  }

  @Override
  public void expression(ExpressionNode node) {
    for (String library : libraries) {
      if (node.getCode().contains(library)) {
        createViolation(node);
      }
    }
  }

  private boolean isIllegalImport(Attribute a) {
    if ("import".equals(a.getName())) {
      for (String library : libraries) {
        if (a.getValue().contains(library)) {
          return true;
        }
      }
    }
    return false;
  }
}