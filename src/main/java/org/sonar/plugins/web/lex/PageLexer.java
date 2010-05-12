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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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

  private static List tokenizers = Arrays.asList(
  /* HTML Comments */
  new CommentTokenizer("<!--", "-->", true),
  /* JSP Comments */
  new CommentTokenizer("<%--", "--%>", false),
  /* HTML Directive */
  new DirectiveTokenizer("<!DOCTYPE", ">", true),
  /* JSP Directives */
  new DirectiveTokenizer("<%@", "%>", false),
  /* JSP Expressions */
//  new TextTokenizer("<%", "%>",),
  /* XML and HTML Tags */
  new ElementTokenizer("<", ">"),
  /* Text (for everything else) */
  new TextTokenizer());

  public List<Node> parse(Reader fileReader) throws FileNotFoundException {
    
    // CodeReader reads the file stream
    CodeReader codeReader = new CodeReader(fileReader);

    // ArrayList collects the nodes
    List<Node> nodeList = new ArrayList();
    
    // ChannelDispatcher manages the tokenizers
    ChannelDispatcher<List<Node>> channelDispatcher = new ChannelDispatcher<List<Node>>(tokenizers);
    channelDispatcher.consum(codeReader, nodeList);

    // clean up
    codeReader.close();

    return nodeList;
  }
}
