/*
 * SonarQube HTML
 * Copyright (C) 2010-2025 SonarSource SA
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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.sonar.plugins.html.node.Attribute;
import org.sonar.plugins.html.node.CommentNode;
import org.sonar.plugins.html.node.DirectiveNode;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.NodeType;
import org.sonar.plugins.html.node.TagNode;
import org.sonar.plugins.html.node.TextNode;
import org.sonar.sslr.channel.CodeReader;

import static org.assertj.core.api.Assertions.assertThat;


class PageLexerTest {

  @Test
  void testLexer() throws FileNotFoundException {

    String fileName = "src/test/resources/src/main/webapp/create-salesorder.xhtml";
    PageLexer lexer = new PageLexer();
    List<Node> nodeList = lexer.parse(new FileReader(fileName));

    assertThat(nodeList).hasSizeGreaterThan(50);

    // check tagnodes
    for (Node node : nodeList) {
      if (node instanceof TagNode) {
        assertThat(node.getCode()).startsWith("<");
        assertThat(node.getCode()).endsWith(">");
      }
    }

    showHierarchy(nodeList);

    // check hierarchy
    for (Node node : nodeList) {
      if (node instanceof TagNode tagNode && !tagNode.isEndElement()) {
          if (tagNode.equalsElementName("define")) {
            assertThat(tagNode.getChildren())
              .as("Tag should have children: " + tagNode.getCode())
              .isNotEmpty();
          } else if (tagNode.equalsElementName("outputText")) {
            assertThat(tagNode.getChildren()).isEmpty();
          }
        }

    }
  }

  @Test
  void testRuby() throws FileNotFoundException {

    String fileName = "src/test/resources/src/main/webapp/select_user.html.erb";
    PageLexer lexer = new PageLexer();
    List<Node> nodeList = lexer.parse(new FileReader(fileName));

    assertThat(nodeList).hasSizeGreaterThan(50);
  }

  private void showHierarchy(List<Node> nodeList) {

    StringBuilder sb = new StringBuilder();
    for (Node node : nodeList) {
      if (node.getClass() == TagNode.class && ((TagNode) node).getParent() == null) {
        TagNode root = (TagNode) node;
        printTag(sb, root, 0);
      }
    }
  }

  private void printTag(StringBuilder sb, TagNode node, int indent) {
    sb.append('\n');
    sb.append(" ".repeat(Math.max(0, indent)));
    sb.append('<');
    sb.append(node.getNodeName());
    if (!node.getChildren().isEmpty()) {
      sb.append('>');
      for (TagNode child : node.getChildren()) {
        printTag(sb, child, indent + 1);
      }
      sb.append('\n');
      sb.append(" ".repeat(Math.max(0, indent)));
      sb.append("</");
      sb.append(node.getNodeName());
      sb.append('>');
    } else {
      sb.append("/>");
    }
  }

  @Test
  void testDirectiveNode() {
    String directive = "<!docTyPE html "
      + "PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">";
    DoctypeTokenizer tokenizer = new DoctypeTokenizer("<!DOCTYPE", ">");
    List<Node> nodeList = new ArrayList<>();
    CodeReader codeReader = new CodeReader(directive);
    tokenizer.consume(codeReader, nodeList);
    assertThat(nodeList).hasSize(1);
    Node node = nodeList.get(0);
    assertThat(node).isInstanceOf(DirectiveNode.class);
    assertThat(((DirectiveNode) node).getAttributes()).hasSize(4);
  }

  @Test
  void testNestedTagInAttribute() {
    String fragment = "<td id=\"typeCellHeader\"<c:if test='${param.typeNormalOrError == \"error\"}'>"
      + "style=\"display:none;\"</c:if>>Type" + "</td>";

    StringReader reader = new StringReader(fragment);
    PageLexer lexer = new PageLexer();
    List<Node> nodeList = lexer.parse(reader);

    assertThat(nodeList).hasSize(3);
    assertThat(nodeList.get(0)).isInstanceOf(TagNode.class);
    assertThat(nodeList.get(1)).isInstanceOfAny(TextNode.class);
    assertThat(nodeList.get(2)).isInstanceOf(TagNode.class);

    TagNode tagNode = (TagNode) nodeList.get(0);
    assertThat(tagNode.getAttributes()).hasSize(4);

    // the embedded tags are added as attributes
    assertThat(tagNode.getAttributes().get(1).getValue()).isEmpty();
    assertThat(tagNode.getAttributes().get(3).getValue()).isEmpty();
  }

  @Test
  void testNestedScriptlet() {
    String fragment = "<option value=\"<%= key -%>\" <%= 'selected' if alert.operator==key -%>>";

    StringReader reader = new StringReader(fragment);
    PageLexer lexer = new PageLexer();
    List<Node> nodeList = lexer.parse(reader);

    assertThat(nodeList).hasSize(1);

    TagNode tagNode = (TagNode) nodeList.get(0);
    assertThat(tagNode.getAttributes()).hasSize(2);

    // the embedded tags are added as attributes
    assertThat(tagNode.getAttributes().get(0).getName()).isEqualTo("value");
    assertThat(tagNode.getAttributes().get(0).getValue()).isEqualTo("<%= key -%>");

    assertThat(tagNode.getAttributes().get(1).getName()).isEqualTo("<%= 'selected' if alert.operator==key -%>");
    assertThat(tagNode.getAttributes().get(1).getValue()).isEmpty();
  }

  @Test
  void testNestedTagInValue() {
    String fragment = "<td label=\"Hello <c:if test='${param == true}'>World</c:if>\">Type</td>";

    StringReader reader = new StringReader(fragment);
    PageLexer lexer = new PageLexer();
    List<Node> nodeList = lexer.parse(reader);

    assertThat(nodeList).hasSize(3);
    assertThat(nodeList.get(0)).isInstanceOf(TagNode.class);
    assertThat(nodeList.get(1)).isInstanceOf(TextNode.class);
    assertThat(nodeList.get(2)).isInstanceOf(TagNode.class);

    TagNode tagNode = (TagNode) nodeList.get(0);
    assertThat(tagNode.getAttributes()).hasSize(1);
  }

  @Test
  void should_recover_on_invalid_attribute() {
    PageLexer lexer = new PageLexer();
    List<Node> nodes = lexer.parse(new StringReader("<foo = bar=42>"));
    assertThat(nodes).hasSize(1);
    assertThat(((TagNode) nodes.get(0)).getAttribute("bar")).isEqualTo("42");
  }

  @Test
  void nestedQuotes() {
    String fragment = "<tr class=\"<c:if test='${count%2==0}'>even</c:if>"
      + "<c:if test='${count%2!=0}'>odd</c:if><c:if test='${ActionType==\"baseline\"}'> baseline</c:if>\">";

    StringReader reader = new StringReader(fragment);
    PageLexer lexer = new PageLexer();
    List<Node> nodeList = lexer.parse(reader);

    assertThat(nodeList).hasSize(1);
    TagNode tagNode = (TagNode) nodeList.get(0);
    assertThat(tagNode.getAttributes()).hasSize(1);
  }

  @Test
  void escapeCharacters() {
    String fragment = "<c:when test=\"${citaflagurge eq \\\"S\\\"}\">";

    StringReader reader = new StringReader(fragment);
    PageLexer lexer = new PageLexer();
    List<Node> nodeList = lexer.parse(reader);

    assertThat(nodeList).hasSize(1);
    TagNode tagNode = (TagNode) nodeList.get(0);
    assertThat(tagNode.getAttributes()).hasSize(1);
  }

  @Test
  void javaScriptWithNestedTags() throws FileNotFoundException {
    String fileName = "src/test/resources/lexer/javascript-nestedtags.jsp";
    PageLexer lexer = new PageLexer();
    List<Node> nodeList = lexer.parse(new FileReader(fileName));
    assertThat(nodeList).hasSize(12);

    // check script node
    Node node = nodeList.get(2);
    assertThat(node).isInstanceOf(TagNode.class);
    TagNode scriptNode = (TagNode) node;
    assertThat(scriptNode.getNodeName()).isEqualTo("script");
    assertThat(scriptNode.getChildren()).isEmpty();
  }

  @Test
  void javaScriptWithComments() throws FileNotFoundException {
    String fileName = "src/test/resources/lexer/script-with-comments.jsp";
    PageLexer lexer = new PageLexer();
    List<Node> nodeList = lexer.parse(new FileReader(fileName));
    assertThat(nodeList).hasSize(3);
  }

  @Test
  void testComment() {
    String fragment = "<!-- text --><p>aaa</p>";

    StringReader reader = new StringReader(fragment);
    PageLexer lexer = new PageLexer();
    List<Node> nodeList = lexer.parse(reader);

    assertThat(nodeList).hasSize(4);
    assertThat(nodeList.get(0)).isInstanceOf(CommentNode.class);
  }

  @Test
  void testNestedComment() {
    String fragment = "<!-- text <!--><p>This is not part of the comment</p>";

    StringReader reader = new StringReader(fragment);
    PageLexer lexer = new PageLexer();
    List<Node> nodeList = lexer.parse(reader);

    assertThat(nodeList).hasSize(4);
    assertThat(nodeList.get(0)).isInstanceOf(CommentNode.class);
    assertThat(nodeList.get(1)).isInstanceOf(TagNode.class);
    assertThat(nodeList.get(2)).isInstanceOf(TextNode.class);
    assertThat(nodeList.get(3)).isInstanceOf(TagNode.class);
  }

  @Test
  void testAttributeWithoutQuotes() {
    final StringReader reader = new StringReader("<img src=http://foo/sfds?sjg a=1\tb=2\r\nc=3 />");
    final PageLexer lexer = new PageLexer();
    final List<Node> nodeList = lexer.parse(reader);

    assertThat(nodeList).hasSize(1);
    assertThat(nodeList.get(0)).isInstanceOf(TagNode.class);
    final TagNode node = (TagNode) nodeList.get(0);
    assertThat(node.getAttributes()).hasSize(4);

    final Attribute attribute = node.getAttributes().get(0);
    assertThat(attribute.getName()).isEqualTo("src");
    assertThat(attribute.getValue()).isEqualTo("http://foo/sfds?sjg");

    final Attribute attributeA = node.getAttributes().get(1);
    assertThat(attributeA.getName()).isEqualTo("a");
    assertThat(attributeA.getValue()).isEqualTo("1");

    final Attribute attributeB = node.getAttributes().get(2);
    assertThat(attributeB.getName()).isEqualTo("b");
    assertThat(attributeB.getValue()).isEqualTo("2");

    final Attribute attributeC = node.getAttributes().get(3);
    assertThat(attributeC.getName()).isEqualTo("c");
    assertThat(attributeC.getValue()).isEqualTo("3");
  }

  @Test
  void attribute_value_starting_with_quote() {
    StringReader reader = new StringReader("<img src=\"'a'\"/>");
    List<Node> nodeList = new PageLexer().parse(reader);
    assertThat(nodeList).hasSize(1);
    assertThat(nodeList.get(0)).isInstanceOf(TagNode.class);
    TagNode node = (TagNode) nodeList.get(0);
    Attribute attribute = node.getAttributes().get(0);
    assertThat(attribute.getName()).isEqualTo("src");
    assertThat(attribute.getValue()).isEqualTo("'a'");
  }

  @Test
  void text_containing_opening_angle_bracket() {
    assertOnlyText("x = '<");
    assertOnlyText("x = '<';");
    assertOnlyText("x = '< ';");
  }

  @Test
  void testUnmatchedClosingElement() {
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
  void testUnmatchedNonHtmlElements() {
    assertNodes("<html><ul><c:if></ul></c:if><li></ul><p>",
      node("html",
        node("ul", node("c:if"), node("li")),
        node("p")
      ));
  }

  @Test
  void testHead() {
    assertNodes("<html><head><title>Foo </title><body></body></html>",
      node("html",
        node("head", node("title")),
        node("body")
      ));
  }

  @Test
  void testLi() {
    assertNodes("<html><ul><li>1 <li>2 <li>3</ul>",
      node("html",
        node("ul", node("li"), node("li"), node("li"))));
  }

  @Test
  void testDtDd() {
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
  void testP() {
    assertNodes("<html><p>P1<table></table><p>P2<p>P3<h1>heading</h1><p>P4",
      node("html",
        node("p"), node("table"), node("p"), node("p"), node("h1"), node("p")));
  }

  @Test
  void testRubyElements() {
    assertNodes("<html><ruby> 漢 <rb>字 <rp> (  <rt>かん  <rt>じ  <rp>) <rtc> <rtc> <rb> </ruby>",
      node("html",
        node("ruby",
          node("rb"), node("rp"), node("rt"), node("rt"), node("rp"), node("rtc"), node("rtc"), node("rb"))));
  }

  @Test
  void testOptgroup() {
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
  void testColgroup() {
    assertNodes("<table><colgroup><col><col><col><template></template><thead>",
      node("table",
        node("colgroup",
          node("col"), node("col"), node("col"), node("template")),
        node("thead"))
    );
  }

  @Test
  void testCaption() {
    assertNodes("<table><caption>Caption <a>link</a><thead>",
      node("table",
        node("caption", node("a")),
        node("thead")
      ));
  }

  @Test
  void testThead() {
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
    assertThat(nodeList)
      .isNotEmpty()
      .allMatch(node -> node.getNodeType() == NodeType.TEXT);
  }

  @Test
  void entity() {
    assertSingleTag("<!ENTITY delta \"&#948;\">");
  }

  @Test
  void cdata() {
    assertSingleTag("<![CDATA[hello]]>");
  }

  @Test
  void tag_with_whitespace_after_name() {
    assertSingleTag("<html  >");
  }

  @Test
  void tag_with_invalid_character_before_name_is_considered_as_text() {
    // Tag names cannot start with a whitespace, a digit or any other invalid character: https://www.w3.org/TR/REC-xml/#sec-starttags.
    assertOnlyText("<  html>");
    assertOnlyText("<5html>");
    assertOnlyText("<←html>");
  }

  @Test
  void start_tag_character_is_considered_as_text_when_followed_by_whitespace() {
    PageLexer lexer = new PageLexer();
    List<Node> nodes = lexer.parse(new StringReader("<a> < a </a>"));
    assertThat(nodes).hasSize(4);
    assertThat(nodes).extracting(Node::getNodeType).containsExactly(NodeType.TAG, NodeType.TEXT, NodeType.TEXT, NodeType.TAG);
    assertThat(nodes).extracting(Node::getCode).containsExactly("<a>", " ", "< a ", "</a>");
  }

  @Test
  void start_tag_character_is_considered_as_text_when_last_character_of_code() {
    PageLexer lexer = new PageLexer();
    List<Node> nodes = lexer.parse(new StringReader("<a> <"));
    assertThat(nodes).hasSize(3);
    assertThat(nodes).extracting(Node::getNodeType).containsExactly(NodeType.TAG, NodeType.TEXT, NodeType.TEXT);
    assertThat(nodes).extracting(Node::getCode).containsExactly("<a>", " ", "<");
  }

  private void assertSingleTag(String code) {
    StringReader reader = new StringReader(code);
    List<Node> nodeList = new PageLexer().parse(reader);
    assertThat(nodeList).hasSize(1);
    assertThat(nodeList.get(0)).isInstanceOf(TagNode.class);
  }
}
