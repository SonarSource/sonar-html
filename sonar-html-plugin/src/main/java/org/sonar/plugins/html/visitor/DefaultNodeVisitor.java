/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (C) 2010-2024 SonarSource SA and Matthijs Galesloot
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
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
