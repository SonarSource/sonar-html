/*
 * Copyright (C) 2010 Matthijs Galesloot
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

package org.sonar.plugins.web.visitor;

import java.util.List;

import org.sonar.plugins.web.node.CommentNode;
import org.sonar.plugins.web.node.DirectiveNode;
import org.sonar.plugins.web.node.ExpressionNode;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.node.TagNode;
import org.sonar.plugins.web.node.TextNode;

/**
 * Defines interface for node visitor with default dummy implementations.
 * 
 * @author Matthijs Galesloot
 */
public abstract class DefaultNodeVisitor implements NodeVisitor {

  private WebSourceCode webSourceCode;

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

  public WebSourceCode getWebSourceCode() {
    return webSourceCode;
  }

  public void startDocument(WebSourceCode webSourceCode, List<Node> nodes) {
    startDocument(webSourceCode);
  }

  public void startDocument(WebSourceCode webSourceCode) {
    this.webSourceCode = webSourceCode;
  }

  public void startElement(TagNode node) {

  }
}
