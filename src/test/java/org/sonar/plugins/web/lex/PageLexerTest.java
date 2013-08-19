/*
 * Sonar Web Plugin
 * Copyright (C) 2010 SonarSource and Matthijs Galesloot
 * dev@sonar.codehaus.org
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

import org.junit.Test;
import org.sonar.channel.CodeReader;
import org.sonar.plugins.web.node.DirectiveNode;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.node.TagNode;
import org.sonar.plugins.web.node.TextNode;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.fest.assertions.Assertions.assertThat;

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

    // check tagnodes
    for (Node node : nodeList) {
      if (node instanceof TagNode) {
        assertTrue(node.getCode().startsWith("<"));
        assertTrue(node.getCode().endsWith(">"));
      }
    }

    showHierarchy(nodeList);

    // check hierarchy
    for (Node node : nodeList) {
      if (node instanceof TagNode) {
        TagNode tagNode = (TagNode) node;

        if (!tagNode.isEndElement()) {
          if (tagNode.equalsElementName("define")) {
            assertTrue("Tag should have children: " + tagNode.getCode(), tagNode.getChildren().size() > 0);
          } else if (tagNode.equalsElementName("outputText")) {
            assertThat(tagNode.getChildren().size()).isEqualTo(0);
          }
        }
      }
    }
  }

  @Test
  public void testRuby() throws FileNotFoundException {

    String fileName = "src/test/resources/src/main/webapp/select_user.html.erb";
    PageLexer lexer = new PageLexer();
    List<Node> nodeList = lexer.parse(new FileReader(fileName));

    assertTrue(nodeList.size() > 50);

    // TODO - better parsing of erb.
  }

  @Test
  public void testDtl() throws FileNotFoundException {

    String fileName = "src/test/resources/src/main/webapp/select_user.html.dtl";
    PageLexer lexer = new PageLexer();
    List<Node> nodeList = lexer.parse(new FileReader(fileName));

    assertTrue(nodeList.size() > 50);
  }

  private void showHierarchy(List<Node> nodeList) {

    StringBuilder sb = new StringBuilder();
    for (Node node : nodeList) {
      if (node.getClass() == TagNode.class && ((TagNode) node).getParent() == null) {
        TagNode root = (TagNode) node;
        printTag(sb, root, 0);
        // System.out.print(sb.toString());
      }
    }
  }

  private void printTag(StringBuilder sb, TagNode node, int indent) {
    sb.append('\n');
    for (int i = 0; i < indent; i++) {
      sb.append(" ");
    }
    sb.append('<');
    sb.append(node.getNodeName());
    if (node.getChildren().size() > 0) {
      sb.append('>');
      for (TagNode child : node.getChildren()) {
        printTag(sb, child, indent + 1);
      }
      sb.append('\n');
      for (int i = 0; i < indent; i++) {
        sb.append(" ");
      }
      sb.append("</");
      sb.append(node.getNodeName());
      sb.append('>');
    } else {
      sb.append("/>");
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
    assertThat(tagNode.getAttributes().get(1).getValue()).isEmpty();
    assertThat(tagNode.getAttributes().get(3).getValue()).isEmpty();
  }

  @Test
  public void testNestedScriptlet() {
    String fragment = "<option value=\"<%= key -%>\" <%= 'selected' if alert.operator==key -%>>";

    StringReader reader = new StringReader(fragment);
    PageLexer lexer = new PageLexer();
    List<Node> nodeList = lexer.parse(reader);

    assertEquals(1, nodeList.size());

    TagNode tagNode = (TagNode) nodeList.get(0);
    assertEquals(2, tagNode.getAttributes().size());

    // the embedded tags are added as attributes
    assertEquals(tagNode.getAttributes().get(0).getName(), "value");
    assertEquals(tagNode.getAttributes().get(0).getValue(), "<%= key -%>");
    assertEquals(tagNode.getAttributes().get(1).getName(), "<%= 'selected' if alert.operator==key -%>");
    assertThat(tagNode.getAttributes().get(1).getValue()).isEmpty();
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

  @Test
  public void javaScriptWithNestedTags() throws FileNotFoundException {
    String fileName = "src/test/resources/lexer/javascript-nestedtags.jsp";
    PageLexer lexer = new PageLexer();
    List<Node> nodeList = lexer.parse(new FileReader(fileName));
    assertEquals(12, nodeList.size());

    // check script node
    Node node = nodeList.get(2);
    assertTrue(node instanceof TagNode);
    TagNode scriptNode = (TagNode) node;
    assertEquals("script", scriptNode.getNodeName());
    assertEquals(0, scriptNode.getChildren().size());
  }

  @Test
  public void javaScriptWithComments() throws FileNotFoundException {
    String fileName = "src/test/resources/lexer/script-with-comments.jsp";
    PageLexer lexer = new PageLexer();
    List<Node> nodeList = lexer.parse(new FileReader(fileName));
    assertEquals(3, nodeList.size());
  }

}
