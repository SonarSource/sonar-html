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

import org.sonar.channel.CodeReader;
import org.sonar.channel.EndMatcher;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.node.TextNode;

/**
 * @author Matthijs Galesloot
 *
 *         TODO - handle CDATA
 */
class TextTokenizer extends AbstractTokenizer<List<Node>> {

  private static final class EndTokenMatcher implements EndMatcher {

    public boolean match(int endFlag) {
      return endFlag == '<';
    }
  }

  private final EndMatcher endTokenMatcher = new EndTokenMatcher();

  public TextTokenizer() {
    super("", "");
  }

  @Override
  public boolean consume(CodeReader codeReader, List<Node> nodeList) {
    Node node = createNode();

    setStartPosition(codeReader, node);

    StringBuilder stringBuilder = new StringBuilder();
    codeReader.popTo(endTokenMatcher, stringBuilder);
    node.setCode(stringBuilder.toString());
    setEndPosition(codeReader, node);

    nodeList.add(node);

    return true;
  }

  @Override
  Node createNode() {
    return new TextNode();
  }

}
