/*
 * SonarQube HTML
 * Copyright (C) 2010-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource Sàrl.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
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
