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

import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.sonar.channel.ChannelDispatcher;
import org.sonar.channel.CodeReader;
import org.sonar.plugins.web.node.Node;

/**
 * @author Matthijs Galesloot
 */
@SuppressWarnings("unchecked")
public final class PageLexer {

  /**
   *  The order of the tokenizers is significant, as they are processed in this order.
   * 
   *  TextTokenizer must be last, it will always consume the characters until the next token arrives.
   */
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
      /* XML and HTML Tags */
      new ElementTokenizer("<", ">"),
      /* Text (for everything else) */
      new TextTokenizer());

  public List<Node> parse(Reader reader) {

    // CodeReader reads the file stream
    CodeReader codeReader = new CodeReader(reader);

    // ArrayList collects the nodes
    List<Node> nodeList = new ArrayList<Node>();

    // ChannelDispatcher manages the tokenizers
    ChannelDispatcher<List<Node>> channelDispatcher = new ChannelDispatcher<List<Node>>(tokenizers);
    channelDispatcher.consume(codeReader, nodeList);

    // clean up
    codeReader.close();

    return nodeList;
  }
}
