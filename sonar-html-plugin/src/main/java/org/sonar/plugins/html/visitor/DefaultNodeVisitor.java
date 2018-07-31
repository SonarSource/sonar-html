/*
 * SonarHTML :: SonarQube Plugin
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
package org.sonar.plugins.html.visitor;

import java.util.List;

import org.sonar.plugins.html.node.CommentNode;
import org.sonar.plugins.html.node.DirectiveNode;
import org.sonar.plugins.html.node.ExpressionNode;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;
import org.sonar.plugins.html.node.TextNode;

/**
 * Defines interface for node visitor with default dummy implementations.
 */
public abstract class DefaultNodeVisitor {

  private HtmlSourceCode htmlSourceCode;

  public void init() {
  }

  public void characters(TextNode textNode) {
  }

  public void comment(CommentNode node) {
  }

  public void directive(DirectiveNode node) {
  }

  public void endDocument() {
  }

  public void endElement(TagNode node) {
  }

  public void expression(ExpressionNode node) {
  }

  public HtmlSourceCode getHtmlSourceCode() {
    return htmlSourceCode;
  }

  public void setSourceCode(HtmlSourceCode sourceCode) {
    this.htmlSourceCode = sourceCode;
  }

  public void startDocument(List<Node> nodes) {
  }

  public void startElement(TagNode node) {
  }

}
