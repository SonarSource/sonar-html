/*
 * Sonar Web Plugin
 * Copyright (C) 2010 Matthijs Galesloot and SonarSource
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
package org.sonar.plugins.web.checks.whitespace;

import org.apache.commons.lang.StringUtils;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.node.TextNode;

/**
 * Checker for Tab character.
 *
 * @see http://java.sun.com/developer/technicalArticles/javaserverpages/code_convention/ paragraph Indentation
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
@Rule(key = "IllegalTabCheck", priority = Priority.MINOR)
public class IllegalTabCheck extends AbstractPageCheck {

  @Override
  public void characters(TextNode textNode) {
    if (StringUtils.contains(textNode.getCode(), '\t')) {
      createViolation(textNode.getStartLinePosition(), "Avoid using the tab character for indentation.");
    }
  }
}
