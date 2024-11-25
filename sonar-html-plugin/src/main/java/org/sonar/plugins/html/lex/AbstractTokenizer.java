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
import org.sonar.plugins.html.node.Node;
import org.sonar.sslr.channel.Channel;
import org.sonar.sslr.channel.CodeReader;
import org.sonar.sslr.channel.EndMatcher;

/**
 * AbstractTokenizer provides basic token parsing.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
abstract class AbstractTokenizer<T extends List<Node>> extends Channel<T> {

  private final class EndTokenMatcher implements EndMatcher {

    private final CodeReader codeReader;
    private boolean quoting;
    private int nesting;

    private EndTokenMatcher(CodeReader codeReader) {
      this.codeReader = codeReader;
    }

    @Override
    public boolean match(int endFlag) {
      if (endFlag == '"') {
        quoting = !quoting;
      }
      if (!quoting) {
        boolean started = equalsIgnoreCase(codeReader.peek(startChars.length), startChars);
        if (started) {
          nesting++;
        } else {
          boolean ended = Arrays.equals(codeReader.peek(endChars.length), endChars);
          if (ended) {
            nesting--;
            return nesting < 0;
          }
        }
      }
      return false;
    }
  }

  private final char[] endChars;

  private final char[] startChars;

  protected AbstractTokenizer(String startChars, String endChars) {
    this.startChars = startChars.toCharArray();
    this.endChars = endChars.toCharArray();
  }

  protected void addNode(List<Node> nodeList, Node node) {
    nodeList.add(node);
  }

  @Override
  public boolean consume(CodeReader codeReader, T nodeList) {
    if (equalsIgnoreCase(codeReader.peek(startChars.length), startChars)) {
      Node node = createNode();
      setStartPosition(codeReader, node);

      StringBuilder stringBuilder = new StringBuilder();
      popTo(codeReader, getEndMatcher(codeReader), stringBuilder);
      for (int i = 0; i < endChars.length; i++) {
        codeReader.pop(stringBuilder);
      }
      node.setCode(stringBuilder.toString());
      setEndPosition(codeReader, node);

      addNode(nodeList, node);

      return true;
    } else {
      return false;
    }
  }

  protected static String popTo(CodeReader codeReader, EndMatcher endMatcher, StringBuilder stringBuilder) {
    boolean shouldContinue = codeReader.peek() != -1;
    while (shouldContinue) {
      stringBuilder.append((char) codeReader.pop());
      shouldContinue = !endMatcher.match(codeReader.peek()) && codeReader.peek() != -1;
    }

    return stringBuilder.toString();
  }

  abstract Node createNode();

  protected final void setEndPosition(CodeReader code, Node node) {
    node.setEndLinePosition(code.getLinePosition());
    node.setEndColumnPosition(code.getColumnPosition());
  }

  protected final void setStartPosition(CodeReader code, Node node) {
    node.setStartLinePosition(code.getLinePosition());
    node.setStartColumnPosition(code.getColumnPosition());
  }

  private static boolean equalsIgnoreCase(char[] a, char[] b) {
    if (a.length != b.length) {
      return false;
    }

    for (int i = 0; i < a.length; i++) {
      if (Character.toLowerCase(a[i]) != Character.toLowerCase(b[i])) {
        return false;
      }
    }

    return true;
  }

  protected EndMatcher getEndMatcher(CodeReader codeReader) {
    return new EndTokenMatcher(codeReader);
  }

}
