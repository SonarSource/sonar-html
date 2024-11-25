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
package org.sonar.plugins.html.lex;

import java.util.Arrays;
import java.util.List;

import org.sonar.plugins.html.node.CommentNode;
import org.sonar.plugins.html.node.Node;
import org.sonar.sslr.channel.CodeReader;
import org.sonar.sslr.channel.EndMatcher;

/**
 * Tokenizer for a HTML or JSP comment.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
class CommentTokenizer<T extends List<Node>> extends AbstractTokenizer<T> {

  private final class EndTokenMatcher implements EndMatcher {

    private final CodeReader codeReader;

    private EndTokenMatcher(CodeReader codeReader) {
      this.codeReader = codeReader;
    }

    @Override
    public boolean match(int endFlag) {
      return Arrays.equals(codeReader.peek(endChars.length), endChars);
    }

  }

  private final Boolean html;
  private final char[] endChars;

  public CommentTokenizer(String startToken, String endToken, Boolean html) {
    super(startToken, endToken);

    this.html = html;
    this.endChars = endToken.toCharArray();
  }

  @Override
  protected EndMatcher getEndMatcher(CodeReader codeReader) {
    return new EndTokenMatcher(codeReader);
  }

  @Override
  Node createNode() {

    CommentNode node = new CommentNode();
    node.setHtml(html);
    return node;
  }
}
