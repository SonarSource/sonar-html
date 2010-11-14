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
