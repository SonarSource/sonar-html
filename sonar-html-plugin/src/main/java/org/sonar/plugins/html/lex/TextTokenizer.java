/*
 * SonarQube HTML
 * Copyright (C) 2010-2024 SonarSource SA
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

import java.util.List;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.NodeType;
import org.sonar.plugins.html.node.TagNode;
import org.sonar.plugins.html.node.TextNode;
import org.sonar.sslr.channel.CodeReader;
import org.sonar.sslr.channel.EndMatcher;

/**
 * Tokenizer for content.
 *

 *
 *        TODO - handle CDATA
 */
class TextTokenizer extends AbstractTokenizer<List<Node>> {

  private static final class EndTokenMatcher implements EndMatcher {

    @Override
    public boolean match(int endFlag) {
      return endFlag == '<';
    }
  }

  private final EndMatcher endTokenMatcher = new EndTokenMatcher();

  public TextTokenizer() {
    super("", "");
  }

  /**
   * Checks for the end of a script block
   */
  private static class EndScriptMatcher implements EndMatcher {

    private final CodeReader codeReader;
    private static final String END_SCRIPT = "</script>";

    public EndScriptMatcher(CodeReader codeReader) {
      this.codeReader = codeReader;
    }

    @Override
    public boolean match(int endFlag) {

      // return true on end of file
      if (endFlag == (char) -1) {
        return true;
      }

      // check for end script
      return (char) endFlag == '<' && END_SCRIPT.equalsIgnoreCase(new String(codeReader.peek(END_SCRIPT.length())));
    }
  }

  @Override
  public boolean consume(CodeReader codeReader, List<Node> nodeList) {
    Node node = createNode();

    setStartPosition(codeReader, node);

    StringBuilder stringBuilder = new StringBuilder();
    if (inScript(nodeList)) {
      popTo(codeReader, new EndScriptMatcher(codeReader), stringBuilder);
    } else {
      popTo(codeReader, endTokenMatcher, stringBuilder);
    }
    node.setCode(stringBuilder.toString());
    setEndPosition(codeReader, node);

    nodeList.add(node);

    return true;
  }

  private static boolean inScript(List<Node> nodeList) {
    if (!nodeList.isEmpty()) {
      Node node = nodeList.get(nodeList.size() - 1);
      if (node.getNodeType() == NodeType.TAG) {
        TagNode tag = (TagNode) node;
        return !tag.isEndElement() && "script".equalsIgnoreCase(tag.getNodeName());
      }
    }
    return false;
  }

  @Override
  Node createNode() {
    return new TextNode();
  }

}
