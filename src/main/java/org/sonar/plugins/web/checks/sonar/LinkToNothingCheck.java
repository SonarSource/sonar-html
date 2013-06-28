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
package org.sonar.plugins.web.checks.sonar;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.node.TagNode;

import java.util.Locale;

@Rule(
  key = "LinkToNothingCheck",
  priority = Priority.MAJOR)
public class LinkToNothingCheck extends AbstractPageCheck {

  @Override
  public void startElement(TagNode node) {
    if (isATag(node) && hasHrefToNothing(node)) {
      createViolation(node.getStartLinePosition(), "Replace this 'href' value by a link to an existing page or anchor.");
    }
  }

  private static boolean isATag(TagNode node) {
    return "A".equals(node.getNodeName().toUpperCase(Locale.ENGLISH));
  }

  private static boolean hasHrefToNothing(TagNode node) {
    String href = node.getAttribute("href");

    return href != null &&
      isPoitingToNothing(href);
  }

  private static boolean isPoitingToNothing(String target) {
    String upperTarget = target.toUpperCase(Locale.ENGLISH);

    return "#".equals(upperTarget) ||
      "JAVASCRIPT:VOID(0)".equals(upperTarget) ||
      "JAVASCRIPT:VOID(0);".equals(upperTarget);
  }

}
