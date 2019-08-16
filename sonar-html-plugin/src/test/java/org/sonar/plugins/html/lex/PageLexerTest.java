/*
 * SonarHTML :: SonarQube Plugin
 * Copyright (c) 2010-2019 SonarSource SA and Matthijs Galesloot
 * sonarqube@googlegroups.com
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
package org.sonar.plugins.html.lex;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.sonar.channel.CodeReader;
import org.sonar.plugins.html.node.Attribute;
import org.sonar.plugins.html.node.CommentNode;
import org.sonar.plugins.html.node.DirectiveNode;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.NodeType;
import org.sonar.plugins.html.node.TagNode;
import org.sonar.plugins.html.node.TextNode;

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
    String directive = "<!docTyPE html "
      + "PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">";
    DoctypeTokenizer tokenizer = new DoctypeTokenizer("<!DOCTYPE", ">");
    List<Node> nodeList = new ArrayList<>();
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
  public void should_recover_on_invalid_attribute() {
    PageLexer lexer = new PageLexer();
    List<Node> nodes = lexer.parse(new StringReader("<foo = bar=42>"));
    assertThat(nodes).hasSize(1);
    assertThat(((TagNode) nodes.get(0)).getAttribute("bar")).isEqualTo("42");
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

  @Test
  public void testComment() {
    String fragment = "<!-- text --><p>aaa</p>";

    StringReader reader = new StringReader(fragment);
    PageLexer lexer = new PageLexer();
    List<Node> nodeList = lexer.parse(reader);

    assertEquals(4, nodeList.size());
    assertTrue(nodeList.get(0) instanceof CommentNode);
  }

  @Test
  public void testNestedComment() {
    String fragment = "<!-- text <!--><p>This is not part of the comment</p>";

    StringReader reader = new StringReader(fragment);
    PageLexer lexer = new PageLexer();
    List<Node> nodeList = lexer.parse(reader);

    assertEquals(4, nodeList.size());
    assertTrue(nodeList.get(0) instanceof CommentNode);
    assertTrue(nodeList.get(1) instanceof TagNode);
    assertTrue(nodeList.get(2) instanceof TextNode);
    assertTrue(nodeList.get(3) instanceof TagNode);
  }

  @Test
  public void testAttributeWithoutQuotes() {
    final StringReader reader = new StringReader("<img src=http://foo/sfds?sjg a=1\tb=2\r\nc=3 />");
    final PageLexer lexer = new PageLexer();
    final List<Node> nodeList = lexer.parse(reader);

    assertEquals(1, nodeList.size());
    assertTrue(nodeList.get(0) instanceof TagNode);
    final TagNode node = (TagNode) nodeList.get(0);
    assertEquals(4, node.getAttributes().size());

    final Attribute attribute = node.getAttributes().get(0);
    assertEquals("src", attribute.getName());
    assertEquals("http://foo/sfds?sjg", attribute.getValue());

    final Attribute attributeA = node.getAttributes().get(1);
    assertEquals("a", attributeA.getName());
    assertEquals("1", attributeA.getValue());

    final Attribute attributeB = node.getAttributes().get(2);
    assertEquals("b", attributeB.getName());
    assertEquals("2", attributeB.getValue());

    final Attribute attributeC = node.getAttributes().get(3);
    assertEquals("c", attributeC.getName());
    assertEquals("3", attributeC.getValue());
  }

  @Test
  public void attribute_value_starting_with_quote() {
    StringReader reader = new StringReader("<img src=\"'a'\"/>");
    List<Node> nodeList = new PageLexer().parse(reader);
    assertEquals(1, nodeList.size());
    assertTrue(nodeList.get(0) instanceof TagNode);
    TagNode node = (TagNode) nodeList.get(0);
    Attribute attribute = node.getAttributes().get(0);
    assertEquals("src", attribute.getName());
    assertEquals("'a'", attribute.getValue());
  }

  @Test
  public void text_containing_opening_angle_bracket() {
    assertOnlyText("x = '<");
    assertOnlyText("x = '<';");
    assertOnlyText("x = '< ';");
  }

  @Test
  public void testUnmatchedClosingElement() {
    assertNodes("<html><table><tr></table><p>",
      node("html",
        node("table", node("tr")),
        node("p")
      ));

    assertNodes("</html>", node("html"));

    assertNodes("<html><p></b>",
      node("html",
        node("p")));
  }

  @Test
  public void testUnmatchedNonHtmlElements() {
    assertNodes("<html><ul><c:if></ul></c:if><li></ul><p>",
      node("html",
        node("ul", node("c:if"), node("li")),
        node("p")
      ));
  }

  @Test
  public void testHead() {
    assertNodes("<html><head><title>Foo </title><body></body></html>",
      node("html",
        node("head", node("title")),
        node("body")
      ));
  }

  @Test
  public void testLi() {
    assertNodes("<html><ul><li>1 <li>2 <li>3</ul>",
      node("html",
        node("ul", node("li"), node("li"), node("li"))));
  }

  @Test
  public void testDtDd() {
    assertNodes("<html><dl>" +
        "<dt>What is my favorite drink? " +
        "<dd>Tea " +
        "<dt>What is my favorite food? " +
        "<dd>Sushi " +
        "<dd>dd1 " +
        "<dd>dd2 " +
        "<dt>dt1 " +
        "<dt>dt2 " +
        "</dl>",
      node("html",
        node("dl", node("dt"), node("dd"), node("dt"), node("dd"), node("dd"), node("dd"), node("dt"), node("dt"))));
  }

  @Test
  public void testP() {
    assertNodes("<html><p>P1<table></table><p>P2<p>P3<h1>heading</h1><p>P4",
      node("html",
        node("p"), node("table"), node("p"), node("p"), node("h1"), node("p")));
  }

  @Test
  public void testRubyElements() {
    assertNodes("<html><ruby> 漢 <rb>字 <rp> (  <rt>かん  <rt>じ  <rp>) <rtc> <rtc> <rb> </ruby>",
      node("html",
        node("ruby",
          node("rb"), node("rp"), node("rt"), node("rt"), node("rp"), node("rtc"), node("rtc"), node("rb"))));
  }

  @Test
  public void testOptgroup() {
    assertNodes("<html>" +
        "<optgroup>" +
        " <option>1" +
        " <option>2" +
        " <option>3" +
        "<optgroup>" +
        " <option>1" +
        " <option>2" +
        " <option>3",
      node("html",
        node("optgroup", node("option"), node("option"), node("option")),
        node("optgroup", node("option"), node("option"), node("option"))));
  }

  @Test
  public void testColgroup() {
    assertNodes("<table><colgroup><col><col><col><template></template><thead>",
      node("table",
        node("colgroup",
          node("col"), node("col"), node("col"), node("template")),
        node("thead"))
    );
  }

  @Test
  public void testCaption() {
    assertNodes("<table><caption>Caption <a>link</a><thead>",
      node("table",
        node("caption", node("a")),
        node("thead")
      ));
  }

  @Test
  public void testThead() {
    assertNodes("<table><thead><tr><tbody>",
      node("table",
        node("thead", node("tr")),
        node("tbody")
      ));

    assertNodes("<table><thead><tr><tfoot>",
      node("table",
        node("thead", node("tr")),
        node("tfoot")
      ));
  }


  private static void assertNodes(String code, TestNode expected) {
    StringReader reader = new StringReader(code);
    List<Node> nodes = new PageLexer().parse(reader);
    assertNodes((TagNode) nodes.get(0), expected);
  }

  private static void assertNodes(TagNode actual, TestNode expected) {
    assertThat(actual.getNodeName()).isEqualTo(expected.nodeName);
    assertThat(actual.getChildren())
      .as(actual.getNodeName() + " children:")
      .hasSize(expected.children.length);
    int i = 0;
    for (TagNode child : actual.getChildren()) {
      assertNodes(child, expected.children[i]);
      i++;
    }
  }

  static class TestNode {
    final String nodeName;
    final TestNode[] children;

    TestNode(String nodeName, TestNode... children) {
      this.nodeName = nodeName;
      this.children = children;
    }
  }

  private static TestNode node(String nodeName, TestNode... children) {
    return new TestNode(nodeName, children);
  }

  private static void assertOnlyText(String code) {
    StringReader reader = new StringReader(code);
    List<Node> nodeList = new PageLexer().parse(reader);
    assertTrue(nodeList.stream().allMatch(node -> node.getNodeType() == NodeType.TEXT));
  }

  @Test
  public void entity() {
    assertSingleTag("<!ENTITY delta \"&#948;\">");
  }

  @Test
  public void cdata() {
    assertSingleTag("<![CDATA[hello]]>");
  }

  @Test
  public void tag_with_whitespace_before_name() {
    assertSingleTag("<  html>");
  }

  private void assertSingleTag(String code) {
    StringReader reader = new StringReader(code);
    List<Node> nodeList = new PageLexer().parse(reader);
    assertEquals(1, nodeList.size());
    assertTrue(nodeList.get(0) instanceof TagNode);
  }
}
