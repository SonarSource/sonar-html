/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (c) 2010-2022 SonarSource SA and Matthijs Galesloot
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
package org.sonar.plugins.html.checks.sonar;

import javax.annotation.Nullable;

import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "TableWithoutCaptionCheck")
public class TableWithoutCaptionCheck extends AbstractPageCheck {

  @Override
  public void startElement(TagNode node) {
    if (isTable(node) && !isIgnored(node) && !hasDescription(node)) {
      createViolation(node, "Add a description to this table.");
    }
  }

  private static boolean isIgnored(TagNode node) {
    return isLayout(node) || isHidden(node);
  }

  private static boolean hasDescription(TagNode node) {
    return hasSummary(node) || hasAriaDescription(node) || hasCaption(node) || isEmbeddedInFigureWithCaption(node.getParent());
  }

  private static boolean isTable(TagNode node) {
    return "TABLE".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isCaption(TagNode node) {
    return "CAPTION".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isFigure(TagNode node) {
    return "FIGURE".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isFigCaption(TagNode node) {
    return "FIGCAPTION".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isLayout(TagNode node) {
    String role = node.getAttribute("ROLE");
    return "PRESENTATION".equalsIgnoreCase(role) || "NONE".equalsIgnoreCase(role);
  }

  private static boolean isHidden(TagNode node) {
    return "TRUE".equalsIgnoreCase(node.getAttribute("ARIA-HIDDEN"));
  }

  private static boolean isEmbeddedInFigureWithCaption(@Nullable TagNode node) {
    if (node == null || isTable(node)) {
      return false;
    } else if (isFigure(node) && hasFigCaption(node)) {
      return true;
    } else {
      return isEmbeddedInFigureWithCaption(node.getParent());
    }
  }

  private static boolean hasSummary(TagNode node) {
    return node.hasProperty("SUMMARY");
  }

  private static boolean hasAriaDescription(TagNode node) {
    return node.hasProperty("ARIA-LABEL")
      || node.hasProperty("ARIA-LABELLEDBY")
      || node.hasProperty("ARIA-DESCRIBEDBY");
  }

  private static boolean hasCaption(TagNode node) {
    return !node.getChildren().isEmpty() && isCaption(node.getChildren().get(0));
  }

  private static boolean hasFigCaption(TagNode node) {
    // node has one child at least
    return isFigCaption(node.getChildren().get(0)) || isFigCaption(node.getChildren().get(node.getChildren().size() - 1));
  }
}
