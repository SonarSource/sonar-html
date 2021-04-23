/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (c) 2010-2021 SonarSource SA and Matthijs Galesloot
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
package org.sonar.plugins.html.core;

import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.api.Trivia;
import java.io.IOException;
import java.util.List;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.cpd.NewCpdTokens;
import org.sonar.api.batch.sensor.highlighting.NewHighlighting;
import org.sonar.api.batch.sensor.highlighting.TypeOfText;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.visitor.DefaultNodeVisitor;

public class HtmlTokensVisitor extends DefaultNodeVisitor {

  private static final Logger LOG = Loggers.get(HtmlTokensVisitor.class);

  private final SensorContext context;

  public HtmlTokensVisitor(SensorContext context) {
    this.context = context;
  }

  @Override
  public void startDocument(List<Node> nodes) {
    try {
      highlightAndDuplicate();
    } catch (IllegalArgumentException e) {
      LOG.warn("Giving up highlighting/handling duplication for file " + getHtmlSourceCode().inputFile(), e);
    }
  }

  private void highlightAndDuplicate() {
    if (!getHtmlSourceCode().shouldComputeMetric()) {
      return;
    }
    NewHighlighting highlighting = context.newHighlighting();
    InputFile inputFile = getHtmlSourceCode().inputFile();
    highlighting.onFile(inputFile);

    NewCpdTokens cpdTokens = context.newCpdTokens();
    cpdTokens.onFile(inputFile);

    String fileContent;
    try {
      fileContent = inputFile.contents();
    } catch (IOException e) {
      throw new IllegalStateException("Cannot read " + inputFile, e);
    }

    for (Token token : HtmlLexer.create(context.fileSystem().encoding()).lex(fileContent)) {
      TokenType tokenType = token.getType();
      if (!tokenType.equals(GenericTokenType.EOF)) {
        TokenLocation tokenLocation = new TokenLocation(token);
        cpdTokens.addToken(tokenLocation.startLine(), tokenLocation.startCharacter(), tokenLocation.endLine(), tokenLocation.endCharacter(), token.getValue());
      }
      if (tokenType.equals(HtmlTokenType.DOCTYPE)) {
        highlight(highlighting, token, TypeOfText.STRUCTURED_COMMENT);
      } else if (tokenType.equals(HtmlTokenType.EXPRESSION)) {
        highlight(highlighting, token, TypeOfText.ANNOTATION);
      } else if (tokenType.equals(HtmlTokenType.TAG)) {
        highlight(highlighting, token, TypeOfText.KEYWORD);
      } else if (tokenType.equals(HtmlTokenType.ATTRIBUTE)) {
        TokenLocation tokenLocation = new TokenLocation(token);
        highlighting.highlight(tokenLocation.startLine(), tokenLocation.startCharacter() + /* = */ 1, tokenLocation.endLine(), tokenLocation.endCharacter(), TypeOfText.STRING);
      }
      for (Trivia trivia : token.getTrivia()) {
        highlight(highlighting, trivia.getToken(), TypeOfText.COMMENT);
      }
    }

    highlighting.save();
    cpdTokens.save();
  }

  private static void highlight(NewHighlighting highlighting, Token token, TypeOfText typeOfText) {
    TokenLocation tokenLocation = new TokenLocation(token);
    highlighting.highlight(tokenLocation.startLine(), tokenLocation.startCharacter(), tokenLocation.endLine(), tokenLocation.endCharacter(), typeOfText);
  }

}
