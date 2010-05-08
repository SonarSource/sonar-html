/*
 * Copyright (C) 2010
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.sonar.channel.ChannelDispatcher;
import org.sonar.channel.CodeReader;

/**
 * @author Matthijs Galesloot
 */
public final class HtmlLexer {

  private static List tokenizers = Arrays.asList(
  /* HTML Comments */
  new BasicTokenizer(HtmlComment.class, "<!--", "-->"),
  /* JSP Comments */
  new BasicTokenizer(HtmlComment.class, "<%--", "--%>"),
  /* HTML Directive */
  new BasicTokenizer(HtmlComment.class, "<!DOCTYPE", ">"),
  /* JSP Directives */
  new BasicTokenizer(JspDirective.class, "<%", "%>"),
  /* XML and HTML Tags */
  new BasicTokenizer(HtmlElement.class, "<", ">"),
  /* Text (for everything else) */
  new TextTokenizer());

  public List<Token> parse(File file) throws FileNotFoundException {
    
    // CodeReader reads the file stream
    CodeReader codeReader = new CodeReader(new FileReader(file));

    // ArrayList collects the tokens
    List<Token> tokenList = new ArrayList();
    
    // ChannelDispatcher manages the tokenizers
    ChannelDispatcher<List<Token>> channelDispatcher = new ChannelDispatcher<List<Token>>(tokenizers);
    channelDispatcher.consum(codeReader, tokenList);

    // clean up
    codeReader.close();

    return tokenList;
  }
}
