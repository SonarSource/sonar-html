/*
 * Copyright (C) 2010 Matthijs Galesloot
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

  private final EndMatcher endTokenMatcher = new EndMatcher() {

    public boolean match(int endFlag) {
      return endFlag == '<';
    }
  };

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
