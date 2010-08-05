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

import static junit.framework.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import org.junit.Test;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.node.TagNode;

/**
 * @author Matthijs Galesloot
 */
public class TestPageLexer {

  @Test
  public void testLexer() throws FileNotFoundException {

    String fileName = "src/test/resources/src/main/webapp/create-salesorder.xhtml";
    PageLexer lexer = new PageLexer();
    List<Node> nodeList = lexer.parse(new FileReader(fileName));

    assertTrue(nodeList.size() > 50);

    for (Node node : nodeList) {
      if (node instanceof TagNode) {
        assertTrue(node.getCode().startsWith("<"));
        assertTrue(node.getCode().endsWith(">"));
      }
    }
  }
}
