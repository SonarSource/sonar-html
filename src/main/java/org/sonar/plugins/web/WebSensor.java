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

package org.sonar.plugins.web;

import java.io.File;

import org.sonar.api.batch.GeneratesViolations;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.plugins.web.language.Web;
import org.sonar.plugins.web.language.WebFile;
import org.sonar.plugins.web.lex.HtmlComment;
import org.sonar.plugins.web.lex.HtmlLexer;
import org.sonar.plugins.web.lex.HtmlTokenList;
import org.sonar.plugins.web.lex.Token;
import org.sonar.plugins.web.rules.checks.HtmlCheck;
import org.sonar.plugins.web.rules.checks.HtmlChecks;
import org.sonar.plugins.web.rules.checks.WebDependency;

/**
 * @author Matthijs Galesloot
 */
public final class WebSensor implements Sensor, GeneratesViolations {

  private RulesProfile profile;

  public WebSensor(RulesProfile profile) {
    this.profile = profile;
  }

  /**
   * This sensor executes on Web projects.
   */
  public boolean shouldExecuteOnProject(Project project) {
    return Web.INSTANCE.equals(project.getLanguage());
  }

  public void analyse(Project project, SensorContext sensorContext) {

    HtmlLexer lexer = new HtmlLexer();
    HtmlTokenList tokenList = new HtmlTokenList();
    lexer.addVisitor(tokenList);
    for (HtmlCheck check : HtmlChecks.getChecks(profile)) {
      lexer.addVisitor(check);
    }
    lexer.addVisitor(new WebDependency(project.getFileSystem()));

    for (File webFile : project.getFileSystem().getSourceFiles(Web.INSTANCE)) {
      tokenList.clear();

      try {
        WebFile resource = WebFile.fromIOFile(webFile, project.getFileSystem().getSourceDirs());

        lexer.parse(sensorContext, resource, webFile);

        // for (Token token : tokenList.getTokens()) {
        // Node node = token.getNode();
        // System.out.print(node.toHtml());
        // }
        computeMetrics(resource, tokenList, sensorContext);
      } catch (Exception e) {
        WebUtils.LOG.error("Could not analyze the file " + webFile.getAbsolutePath(), e);
      }
    }
  }

  private void computeMetrics(Resource resource, HtmlTokenList tokenList, SensorContext sensorContext) {
    int linesOfCode = 0;
    int commentLines = 0;
    int blankLines = 0;

    for (int i = 0; i < tokenList.getTokens().size(); i++) {
      Token token = tokenList.getTokens().get(i);

      int thisTokenLines = token.getLinesOfCode();
      linesOfCode += thisTokenLines;

      if (token.isBlank()) {
        blankLines += thisTokenLines;
      } else {

        int appendedBlankLines = 0;
        if (i < tokenList.getTokens().size() - 1 && tokenList.getTokens().get(i + 1).isBlank()) {
          appendedBlankLines = tokenList.getTokens().get(i + 1).getLinesOfCode();
          linesOfCode += appendedBlankLines;
          i++;
        }

        if (token instanceof HtmlComment) {
          commentLines += thisTokenLines;
          if (appendedBlankLines > 0) {
            // WebUtils.LOG.debug("Comment followed by: ##" + tokenList.getTokens().get(i + 1).getCode() + "##");

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

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}
