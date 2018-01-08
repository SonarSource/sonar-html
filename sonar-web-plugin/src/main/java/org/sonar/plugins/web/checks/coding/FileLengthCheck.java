/*
 * SonarSource :: Web :: Sonar Plugin
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
package org.sonar.plugins.web.checks.coding;

import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.node.CommentNode;
import org.sonar.plugins.web.node.DirectiveNode;
import org.sonar.plugins.web.node.ExpressionNode;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.node.TagNode;
import org.sonar.plugins.web.node.TextNode;

import java.util.List;

@Rule(key = "FileLengthCheck")
public class FileLengthCheck extends AbstractPageCheck {

  private static final int DEFAULT_MAX_FILE_LENGTH = 1000;

  @RuleProperty(
    key = "maxLength",
    description = "Maximum authorized lines in a file.",
    defaultValue = "" + DEFAULT_MAX_FILE_LENGTH)
  public int maxLength = DEFAULT_MAX_FILE_LENGTH;

  private int maxLine = 0;

  @Override
  public void startDocument(List<Node> nodes) {
    maxLine = 0;
  }

  @Override
  public void startElement(TagNode node) {
    setMaxLine(node.getEndLinePosition());
  }

  @Override
  public void endElement(TagNode node) {
    setMaxLine(node.getEndLinePosition());
  }

  @Override
  public void characters(TextNode node) {
    setMaxLine(node.getEndLinePosition());
  }

  @Override
  public void comment(CommentNode node) {
    setMaxLine(node.getEndLinePosition());
  }

  @Override
  public void directive(DirectiveNode node) {
    setMaxLine(node.getEndLinePosition());
  }

  @Override
  public void expression(ExpressionNode node) {
    setMaxLine(node.getEndLinePosition());
  }

  private void setMaxLine(int line) {
    if (line > maxLine) {
      maxLine = line;
    }
  }

  @Override
  public void endDocument() {
    if (maxLine > maxLength) {
      createViolation(0, "Current file has " + maxLine + " lines, which is greater than " + maxLength + " authorized. Split it into smaller files.");
    }
  }

}
