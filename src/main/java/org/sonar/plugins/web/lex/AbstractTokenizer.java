/*
 * Sonar Web Plugin
 * Copyright (C) 2010 Matthijs Galesloot
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.plugins.web.lex;

import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.sonar.channel.Channel;
import org.sonar.channel.CodeReader;
import org.sonar.channel.EndMatcher;
import org.sonar.plugins.web.node.Node;

/**
 * @author Matthijs Galesloot
 */
abstract class AbstractTokenizer<T extends List<Node>> extends Channel<T> {

  private final class EndTokenMatcher implements EndMatcher {

    private final CodeReader codeReader;
    private boolean quoting;
    private int nesting;

    private EndTokenMatcher(CodeReader codeReader) {
      this.codeReader = codeReader;
    }

    public boolean match(int endFlag) {
      if (endFlag == '"') {
        quoting = !quoting;
      }
      if (!quoting) {
        boolean started = ArrayUtils.isEquals(codeReader.peek(startChars.length), startChars);
        if (started) {
          nesting++;
        } else {
          boolean ended = ArrayUtils.isEquals(codeReader.peek(endChars.length), endChars);
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

  public AbstractTokenizer(String startChars, String endChars) {
    this.startChars = startChars.toCharArray();
    this.endChars = endChars.toCharArray();
  }

  protected void addNode(List<Node> nodeList, Node node) {
    nodeList.add(node);
  }

  @Override
  public boolean consume(CodeReader codeReader, T nodeList) {
    if (ArrayUtils.isEquals(codeReader.peek(startChars.length), startChars)) {
      Node node = createNode();
      setStartPosition(codeReader, node);

      StringBuilder stringBuilder = new StringBuilder();
      codeReader.popTo(new EndTokenMatcher(codeReader), stringBuilder);
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

  abstract Node createNode();

  protected final void setEndPosition(CodeReader code, Node node) {
    node.setEndLinePosition(code.getLinePosition());
    node.setEndColumnPosition(code.getColumnPosition());
  }

  protected final void setStartPosition(CodeReader code, Node node) {
    node.setStartLinePosition(code.getLinePosition());
    node.setStartColumnPosition(code.getColumnPosition());
  }
}
