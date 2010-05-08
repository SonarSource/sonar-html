/*
 * Copyright (C) 2010
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
import org.sonar.plugins.web.lex.HtmlComment;
import org.sonar.plugins.web.lex.HtmlElement;
import org.sonar.plugins.web.lex.Token;

/**
 * @author Matthijs Galesloot
 */
public class HtmlCountLines extends HtmlVisitor {

  private int elementLines;
  private int blankLines;
  private int commentLines;
  private Class<?> currentElementType;

  @Override
  public void startElement(Token token) {
    
    currentElementType = token.getClass();
      
    int linesOfCode = token.getLinesOfCode();
    if (token instanceof HtmlComment) {
      commentLines += linesOfCode;
      currentElementType = token.getClass();
    } else if (token.isBlank()) {
      
      if (HtmlComment.class.equals(currentElementType)) {
        commentLines++;
        linesOfCode--;
      } else if (HtmlElement.class.equals(currentElementType)) {
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
