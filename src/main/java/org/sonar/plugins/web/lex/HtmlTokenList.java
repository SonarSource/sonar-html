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

package org.sonar.plugins.web.lex;

import java.util.ArrayList;
import java.util.List;

import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.resources.Resource;
import org.sonar.plugins.web.WebUtils;
import org.sonar.plugins.web.language.WebFile;

/**
 * @author Matthijs Galesloot
 */
public class HtmlTokenList extends HtmlVisitor {

  private final List<Token> tokens = new ArrayList<Token>();

  public List<Token> getTokens() {
    return tokens;
  }

  @Override
  public void startElement(Token token) {
    tokens.add(token);
  }

  @Override
  public void startDocument(SensorContext sensorContext, WebFile resource) {
    super.startDocument(sensorContext, resource);
    tokens.clear();
  }

  @Override
  public void endDocument(SensorContext sensorContext, WebFile resource) {
    super.endDocument(sensorContext, resource);

    computeMetrics(sensorContext, resource);
  }

  private void computeMetrics(SensorContext sensorContext, Resource resource) {
    int linesOfCode = 0;
    int commentLines = 0;
    int blankLines = 0;

    for (int i = 0; i < tokens.size(); i++) {
      Token token = tokens.get(i);

      int thisTokenLines = token.getLinesOfCode();
      linesOfCode += thisTokenLines;

      if (token.isBlank()) {
        blankLines += thisTokenLines;
      } else {

        int appendedBlankLines = 0;
        if (i < tokens.size() - 1 && tokens.get(i + 1).isBlank()) {
          appendedBlankLines = tokens.get(i + 1).getLinesOfCode();
          linesOfCode += appendedBlankLines;
          i++;
        }

        if (token instanceof HtmlComment) {
          commentLines += thisTokenLines;
          if (appendedBlankLines > 0) {
            // WebUtils.LOG.debug("Comment followed by: ##" + tokens.get(i + 1).getCode() + "##");

            commentLines++;
          }
        }
        if (appendedBlankLines > 1) {
          blankLines += appendedBlankLines - 1;
        }
      }
    }

    sensorContext.saveMeasure(resource, CoreMetrics.LINES, (double) linesOfCode);
    sensorContext.saveMeasure(resource, CoreMetrics.NCLOC, (double) linesOfCode - commentLines - blankLines);
    sensorContext.saveMeasure(resource, CoreMetrics.COMMENT_LINES, (double) commentLines);

    WebUtils.LOG.debug("WebSensor: " + resource.getLongName() + ":" + linesOfCode + "," + commentLines + "," + blankLines);
  }

}
