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

import java.io.Reader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.NodeType;
import org.sonar.plugins.html.node.TagNode;
import org.sonar.plugins.html.node.TextNode;
import org.sonar.sslr.channel.Channel;
import org.sonar.sslr.channel.ChannelDispatcher;
import org.sonar.sslr.channel.CodeReader;

/**
 * Lexical analysis of a web page.

 */
@SuppressWarnings("unchecked")
public class PageLexer {

  /**
   * The order of the tokenizers is significant, as they are processed in this order.
   * <p>
   * TextTokenizer must be last, it will always consume the characters until the next token arrives.
   */
  @SuppressWarnings("rawtypes")
  private static List tokenizers = Arrays.asList(
    /* HTML Comments */
    new CommentTokenizer("<!--", "-->", true),
    /* JSP Comments */
    new CommentTokenizer("<%--", "--%>", false),
    /* HTML Directive */
    new DoctypeTokenizer("<!DOCTYPE", ">"),
    /* XML Directives */
    new DirectiveTokenizer("<?", "?>"),
    /* JSP Directives */
    new DirectiveTokenizer("<%@", "%>"),
    /* JSP Expressions */
    new ExpressionTokenizer("<%", "%>"),
    /* CDATA */
    new CdataTokenizer(),
    /* XML and HTML Tags */
    new NormalElementTokenizer(),
    /* Text (for everything else) */
    new TextTokenizer());

  /**
   * Void elements can't have any content
   * See https://html.spec.whatwg.org/multipage/syntax.html#void-elements
   */
  private static final Set<String> VOID_ELEMENTS = new HashSet<>(Arrays.asList("area", "base", "br", "col", "embed",
    "hr", "img", "input", "link", "meta", "param", "source", "track", "wbr"));

  private static final Set<String> METADATA_CONTENT = new HashSet<>(Arrays.asList("base", "link", "meta", "noscript",
    "script", "style", "template", "title"));

  private static final Set<String> PARAGRAPH_CLOSING = new HashSet<>(Arrays.asList("address", "article", "aside",
    "blockquote", "details", "div", "dl", "fieldset", "figcaption", "figure", "footer", "form", "h1", "h2", "h3", "h4",
    "h5", "h6", "header", "hr", "main", "nav", "ol", "p", "pre", "section", "table", "ul"));

  private static final Set<String> RUBY_CLOSING = new HashSet<>(Arrays.asList("rtc", "rb", "rp", "rt"));

  private static final Set<String> TABLE_DESCENDANTS = new HashSet<>(Arrays.asList("caption", "colgroup", "thead",
    "tbody", "tr", "tfoot"));

  private static final Set<String> HTML_ELEMENTS = new HashSet<>(Arrays.asList("a", "abbr", "acronym", "address",
    "applet", "area", "article", "aside", "audio", "b", "base", "basefont", "bdi", "bdo", "bgsound", "big", "blink",
    "blockquote", "body", "br", "button", "canvas", "caption", "center", "cite", "code", "col", "colgroup", "content",
    "data", "datalist", "dd", "del", "details", "dfn", "dialog", "dir", "div", "dl", "dt", "em", "embed", "fieldset",
    "figcaption", "figure", "font", "footer", "form", "frame", "frameset", "h1", "h2", "h3", "h4", "h5", "h6", "head",
    "header", "hgroup", "hr", "html", "i", "iframe", "img", "input", "ins", "isindex", "kbd", "keygen", "label",
    "legend", "li", "link", "listing", "main", "map", "mark", "marquee", "menu", "menuitem", "meta", "meter", "nav",
    "nobr", "noframes", "noscript", "object", "ol", "optgroup", "option", "output", "p", "param", "picture",
    "plaintext", "pre", "progress", "q", "rb", "rp", "rt", "rtc", "ruby", "s", "samp", "script", "section", "select",
    "shadow", "slot", "small", "source", "spacer", "span", "strike", "strong", "style", "sub", "summary", "sup", "table",
    "tbody", "td", "template", "textarea", "tfoot", "th", "thead", "time", "title", "tr", "track", "tt", "u", "ul",
    "var", "video", "wbr", "xmp"));

  /**
   * Parse a nested node.
   */
  @SuppressWarnings("rawtypes")
  public List<Node> nestedParse(CodeReader reader) {
    List<Node> nodeList = new ArrayList<>();
    for (AbstractTokenizer tokenizer : (List<AbstractTokenizer>) tokenizers) {
      if (tokenizer.consume(reader, nodeList)) {
        break;
      }
    }
    return nodeList;
  }

  /**
   * Parse the input into a list of tokens, with parent/child relations between the tokens.
   */
  public List<Node> parse(Reader reader) {

    // CodeReader reads the file stream
    CodeReader codeReader = new CodeReader(reader);

    // ArrayList collects the nodes
    List<Node> nodeList = new ArrayList<>();

    // ChannelDispatcher manages the tokenizers
    ChannelDispatcher<List<Node>> channelDispatcher = ChannelDispatcher.builder().addChannels((Channel[]) tokenizers.toArray(new Channel[tokenizers.size()])).build();
    channelDispatcher.consume(codeReader, nodeList);

    createNodeHierarchy(nodeList);

    return nodeList;
  }

  /**
   * Scan the nodes and build the hierarchy of parent and child nodes.
   */
  private static void createNodeHierarchy(List<Node> nodeList) {
    Deque<TagNode> openElementStack = new ArrayDeque<>();
    for (Node node : nodeList) {
      if (node.getNodeType() == NodeType.TEXT) {
        // Set a link to the parent if any is available
        var text = (TextNode) node;
        if (!openElementStack.isEmpty()) {
          text.setParent(openElementStack.peek());
        }
      } else if (node.getNodeType() == NodeType.TAG) {
        var element = (TagNode) node;
        // start element
        if (!element.isEndElement()) {
          TagNode parent = openElementStack.peek();
          while (parent != null
                  && (shouldCloseParent(nodeName(element), nodeName(parent)) || isVoidElement(parent))) {
            openElementStack.pop();
            parent = openElementStack.peek();
          }
          element.setParent(parent);
          openElementStack.push(element);
        }

        // end element
        if (isEndElement(element) && !openElementStack.isEmpty()) {
          TagNode openElement = openElementStack.peek();
          if (openElement.equalsElementName(element.getNodeName())) {
            openElementStack.pop();
          } else {
            // non-well formed, close HTML elements if there is matching open element
            if (openElementStack.stream().anyMatch(tag -> tag.equalsElementName(element.getNodeName()))) {
              while (!openElement.equalsElementName(element.getNodeName()) && isHtmlElement(openElement)) {
                openElement = openElementStack.pop();
              }
            }
          }
        }
      }
    }
  }

  private static boolean isVoidElement(TagNode parent) {
    return VOID_ELEMENTS.contains(nodeName(parent));
  }

  private static boolean isHtmlElement(TagNode parent) {
    return HTML_ELEMENTS.contains(nodeName(parent));
  }

  private static boolean isEndElement(TagNode element) {
    return element.isEndElement() || element.hasEnd();
  }

  private static boolean shouldCloseParent(String element, String parent) {
    // see https://www.w3.org/TR/html52/syntax.html#optional-start-and-end-tags
    switch (parent) {
      case "head":
        return !METADATA_CONTENT.contains(element);
      case "li":
        return "li".equals(element);
      case "dt", "dd":
        return "dt".equals(element) || "dd".equals(element);
      case "p":
        // note that we don't validate the parent of the <p> as described in spec
        return PARAGRAPH_CLOSING.contains(element);
      case "rb", "rp", "rt":
        return RUBY_CLOSING.contains(element);
      case "rtc":
        return "rb".equals(element) || "rtc".equals(element);
      case "optgroup":
        return "optgroup".equals(element);
      case "option":
        return "option".equals(element) || "optgroup".equals(element);
      case "colgroup":
        return !("col".equals(element) || "template".equals(element));
      case "caption":
        return TABLE_DESCENDANTS.contains(element);
      case "thead", "tbody":
        return "tbody".equals(element) || "tfoot".equals(element);
      case "tr":
        return !("td".equals(element) || "th".equals(element));
      case "td", "th":
        return "td".equals(element) || "th".equals(element);
      default:
        return false;
    }
  }

  private static String nodeName(TagNode node) {
    return node.getNodeName().toLowerCase(Locale.ROOT);
  }
}
