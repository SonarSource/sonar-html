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

import org.apache.commons.lang.ArrayUtils;
import org.sonar.channel.Channel;
import org.sonar.channel.CodeReader;
import org.sonar.channel.EndMatcher;
import org.sonar.plugins.web.node.Node;

/**
 * @author Matthijs Galesloot
 */
abstract class AbstractTokenizer implements Channel<List<Node>> {

  private char[] startChars;
  private char[] endChars;
  
  abstract Node createNode();

  public AbstractTokenizer(String startChars, String endChars) {
    this.startChars = startChars.toCharArray();
    this.endChars = endChars.toCharArray();
  }

  private final class EndTokenMatcher implements EndMatcher {

    private CodeReader codeReader;
    private boolean quoting; 
    
    private EndTokenMatcher(CodeReader codeReader) {
      this.codeReader = codeReader;
    }

    public boolean match(int endFlag) {
      if (endFlag == '"') {
        quoting = !quoting; 
      }
      return !quoting && endFlag == endChars[0] && ArrayUtils.isEquals(codeReader.peek(endChars.length), endChars);
    }
  }

  public boolean consum(CodeReader codeReader, List<Node> nodeList) {
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
  
  protected void addNode(List<Node> nodeList, Node node) {
    nodeList.add(node);
  }
  
  protected void setEndPosition(CodeReader code, Node node) {
    node.setEndLinePosition(code.getLinePosition());
    node.setEndColumnPosition(code.getColumnPosition());
  }

  protected void setStartPosition(CodeReader code, Node node) {
    node.setStartLinePosition(code.getLinePosition());
    node.setStartColumnPosition(code.getColumnPosition());
  }
}
