/*
 * SonarSource :: Web :: Sonar Plugin
 * Copyright (c) 2010-2017 SonarSource SA and Matthijs Galesloot
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
package org.sonar.plugins.web.checks.sonar;

import org.sonar.check.Rule;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.node.TagNode;

import java.util.List;

@Rule(key = "PageWithoutFaviconCheck")
public class PageWithoutFaviconCheck extends AbstractPageCheck {

  private int currentHeadTagLine;
  private boolean foundTitleTag;

  @Override
  public void startDocument(List<Node> nodes) {
    currentHeadTagLine = 0;
  }

  @Override
  public void startElement(TagNode node) {
    if (isHeadTag(node)) {
      currentHeadTagLine = node.getStartLinePosition();
      foundTitleTag = false;
    } else if (currentHeadTagLine != 0 && isFaviconTag(node)) {
      foundTitleTag = true;
    }
  }

  @Override
  public void endElement(TagNode node) {
    int line = 0;

    if (isHeadTag(node)) {
      line = currentHeadTagLine;
      currentHeadTagLine = 0;
    }

    if (!foundTitleTag && line != 0) {
      createViolation(line, "Add a 'favicon' declaration in this 'header' tag.");
    }
  }

  private static boolean isHeadTag(TagNode node) {
    return "HEAD".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isFaviconTag(TagNode node) {
    String rel = node.getAttribute("rel");

    return isLinkTag(node) &&
      rel != null &&
      ("ICON".equalsIgnoreCase(rel) || "SHORTCUT ICON".equalsIgnoreCase(rel));
  }

  private static boolean isLinkTag(TagNode node) {
    return "LINK".equalsIgnoreCase(node.getNodeName());
  }

}
