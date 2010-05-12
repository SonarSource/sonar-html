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

import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.plugins.web.WebUtils;
import org.sonar.plugins.web.language.WebFile;
import org.sonar.plugins.web.node.CommentNode;
import org.sonar.plugins.web.node.DirectiveNode;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.node.TagNode;
import org.sonar.plugins.web.node.TextNode;

/**
 * @author Matthijs Galesloot
 */
public class PageCountLines extends AbstractTokenVisitor {

  private int elementLines;
  private int blankLines;
  private int commentLines;
  private Class<?> currentElementType;

  @Override
  public void startElement(TagNode element) {
    handleToken(element);
  }

  @Override
  public void endElement(TagNode element) {
    handleToken(element);
  }

  @Override
  public void characters(TextNode textNode) {
    handleToken(textNode);
  }

  @Override
  public void comment(CommentNode commentNode) {
    handleToken(commentNode);
  }
  
  @Override
  public void directive(DirectiveNode node) {
    handleToken(node);
  }

  private void handleToken(Node node) {
    currentElementType = node.getClass();

    int linesOfCode = node.getLinesOfCode();
    if (node instanceof CommentNode) {
      commentLines += linesOfCode;
      currentElementType = node.getClass();
    } else if (node instanceof TextNode && ((TextNode) node).isBlank()) {

      if (CommentNode.class.equals(currentElementType)) {
        commentLines++;
        linesOfCode--;
      } else if (TagNode.class.equals(currentElementType)) {
        elementLines++;
        linesOfCode--;
      }
      if (linesOfCode > 0) {
        blankLines += linesOfCode;
      }
    }
  }

  @Override
  public void startDocument(SensorContext sensorContext, WebFile resource) {
    super.startDocument(sensorContext, resource);

    elementLines = 0;
    blankLines = 0;
    commentLines = 0;
    currentElementType = null;
  }

  @Override
  public void endDocument() {
    super.endDocument();

    computeMetrics();
  }

  private void computeMetrics() {

    getSensorContext().saveMeasure(getResource(), CoreMetrics.LINES, (double) elementLines + commentLines + blankLines);
    getSensorContext().saveMeasure(getResource(), CoreMetrics.NCLOC, (double) elementLines);
    getSensorContext().saveMeasure(getResource(), CoreMetrics.COMMENT_LINES, (double) commentLines);

    WebUtils.LOG.debug("WebSensor: " + getResource().getLongName() + ":" + elementLines + "," + commentLines + "," + blankLines);
  }

}
