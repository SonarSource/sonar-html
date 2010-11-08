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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNull;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.sonar.channel.CodeReader;
import org.sonar.plugins.web.node.DirectiveNode;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.node.TagNode;
import org.sonar.plugins.web.node.TextNode;

/**
 * @author Matthijs Galesloot
 */
public class PageLexerTest {

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

  @Test
  public void testDirectiveNode() {
    String directive = "<!DOCTYPE html "
        + "PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">";
    DoctypeTokenizer tokenizer = new DoctypeTokenizer("<!DOCTYPE", ">");
    List<Node> nodeList = new ArrayList<Node>();
    CodeReader codeReader = new CodeReader(directive);
    tokenizer.consume(codeReader, nodeList);
    assertEquals(nodeList.size(), 1);
    Node node = nodeList.get(0);
    assertEquals(node.getClass(), DirectiveNode.class);
    DirectiveNode directiveNode = (DirectiveNode) node;
    assertEquals(4, directiveNode.getAttributes().size());
  }

  @Test
  public void testNestedTagInAttribute() {
    String fragment = "<td id=\"typeCellHeader\"<c:if test='${param.typeNormalOrError == \"error\"}'>"
        + "style=\"display:none;\"</c:if>>Type" + "</td>";

    StringReader reader = new StringReader(fragment);
    PageLexer lexer = new PageLexer();
    List<Node> nodeList = lexer.parse(reader);

    assertEquals(3, nodeList.size());
    assertTrue(nodeList.get(0) instanceof TagNode);
    assertTrue(nodeList.get(1) instanceof TextNode);
    assertTrue(nodeList.get(2) instanceof TagNode);

    TagNode tagNode = (TagNode) nodeList.get(0);
    assertEquals(4, tagNode.getAttributes().size());

    // the embedded tags are added as attributes
    assertNull(tagNode.getAttributes().get(1).getValue());
    assertNull(tagNode.getAttributes().get(3).getValue());
  }

  @Test
  public void testNestedTagInValue() {
    String fragment = "<td label=\"Hello <c:if test='${param == true}'>World</c:if>\">Type</td>";

    StringReader reader = new StringReader(fragment);
    PageLexer lexer = new PageLexer();
    List<Node> nodeList = lexer.parse(reader);

    assertEquals(3, nodeList.size());
    assertTrue(nodeList.get(0) instanceof TagNode);
    assertTrue(nodeList.get(1) instanceof TextNode);
    assertTrue(nodeList.get(2) instanceof TagNode);

    TagNode tagNode = (TagNode) nodeList.get(0);
    assertEquals(1, tagNode.getAttributes().size());
  }

  @Test
  public void nestedQuotes() {
    String fragment = "<tr class=\"<c:if test='${count%2==0}'>even</c:if>"
        + "<c:if test='${count%2!=0}'>odd</c:if><c:if test='${ActionType==\"baseline\"}'> baseline</c:if>\">";

    StringReader reader = new StringReader(fragment);
    PageLexer lexer = new PageLexer();
    List<Node> nodeList = lexer.parse(reader);

    assertEquals(1, nodeList.size());
    TagNode tagNode = (TagNode) nodeList.get(0);
    assertEquals(1, tagNode.getAttributes().size());
  }

  @Test
  public void escapeCharacters() {
    String fragment = "<c:when test=\"${citaflagurge eq \\\"S\\\"}\">";

    StringReader reader = new StringReader(fragment);
    PageLexer lexer = new PageLexer();
    List<Node> nodeList = lexer.parse(reader);

    assertEquals(1, nodeList.size());
    TagNode tagNode = (TagNode) nodeList.get(0);
    assertEquals(1, tagNode.getAttributes().size());
  }
}
