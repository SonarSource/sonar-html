/*
 * SonarQube HTML
 * Copyright (C) SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * You can redistribute and/or modify this program under the terms of
 * the Sonar Source-Available License Version 1, as published by SonarSource Sàrl.
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonarsource.analyzer.commons.SonarResolve;
import org.sonar.plugins.html.node.CommentNode;

public class SonarResolveScanner extends DefaultNodeVisitor {

  private static final Logger LOG = LoggerFactory.getLogger(SonarResolveScanner.class);

  private final SensorContext context;

  public SonarResolveScanner(SensorContext context) {
    this.context = context;
  }

  @Override
  public void comment(CommentNode node) {
    String[] commentLines = stripCommentDelimiters(node).split("\\R", -1);
    int index = 0;
    while (index < commentLines.length) {
      int directiveLine = node.getStartLinePosition() + index;
      String line = commentLines[index];
      if (!startsWithDirectiveKeyword(line)) {
        index++;
        continue;
      }

      SonarResolve.StreamingParser parser = new SonarResolve.StreamingParser(directiveLine);
      SonarResolve.StreamingParser.State state = parser.consumeLine(directiveLine, line);
      int lastConsumedIndex = index;

      while (state == SonarResolve.StreamingParser.State.INCOMPLETE && lastConsumedIndex + 1 < commentLines.length) {
        lastConsumedIndex++;
        int lineNumber = node.getStartLinePosition() + lastConsumedIndex;
        state = parser.consumeLine(lineNumber, commentLines[lastConsumedIndex]);
      }

      if (state == SonarResolve.StreamingParser.State.COMPLETE) {
        saveIssueResolution(parser.result());
      } else if (state == SonarResolve.StreamingParser.State.INVALID) {
        logInvalidDirective(directiveLine, parser.errorMessage());
      } else if (parser.finish() == SonarResolve.StreamingParser.State.INVALID) {
        logInvalidDirective(directiveLine, parser.errorMessage());
      }

      index = lastConsumedIndex + 1;
    }
  }

  private static boolean startsWithDirectiveKeyword(String line) {
    String trimmed = line.stripLeading();
    return trimmed.regionMatches(true, 0, SonarResolve.KEYWORD, 0, SonarResolve.KEYWORD.length());
  }

  private void saveIssueResolution(SonarResolve sonarResolve) {
    InputFile inputFile = getHtmlSourceCode().inputFile();
    context.newIssueResolution()
      .on(inputFile)
      .at(inputFile.selectLine(sonarResolve.targetLine()))
      .status(sonarResolve.status())
      .forRules(sonarResolve.ruleKeys())
      .comment(sonarResolve.justification())
      .save();
  }

  private void logInvalidDirective(int line, String errorMessage) {
    LOG.warn("{} in file {} at line {}", errorMessage, getHtmlSourceCode().inputFile(), line);
  }

  private static String stripCommentDelimiters(CommentNode node) {
    String code = node.getCode();
    if (node.isHtml()) {
      return code.substring("<!--".length(), code.length() - "-->".length());
    }
    if (code.startsWith("{#")) {
      return code.substring("{#".length(), code.length() - "#}".length());
    }
    return code.substring("<%--".length(), code.length() - "--%>".length());
  }
}
