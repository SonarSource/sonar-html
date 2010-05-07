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

import org.sonar.api.batch.SensorContext;
import org.sonar.channel.ChannelDispatcher;
import org.sonar.channel.CodeReader;
import org.sonar.plugins.web.language.WebFile;

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
  /* Tags */
  new BasicTokenizer(HtmlElement.class, "<", ">"),
  /* Text (for everything else) */
  new TextTokenizer());

  public void parse(SensorContext sensorContext, WebFile resource, File file) throws FileNotFoundException {

    // notify the visitors for a new document
    for (HtmlVisitor visitor : visitors) {
      visitor.startDocument(sensorContext, resource);
    }

    // CodeReader reads the file stream
    CodeReader codeReader = new CodeReader(new FileReader(file));

    // ChannelDispatcher manages the tokenizers
    ChannelDispatcher<HtmlLexer> channelDispatcher = new ChannelDispatcher<HtmlLexer>(tokenizers);
    channelDispatcher.consum(codeReader, this);

    // clean up
    codeReader.close();
    
    // notify the visitors for end of document
    for (HtmlVisitor visitor : visitors) {
      visitor.endDocument(sensorContext, resource);
    }
  }

  private List<HtmlVisitor> visitors = new ArrayList<HtmlVisitor>();

  public void addVisitor(HtmlVisitor visitor) {
    visitors.add(visitor);
  }

  /**
   * Callback from ChannelDispatcher --> Tokenizer[]
   */
  public void produce(Token token) {
    for (HtmlVisitor visitor : visitors) {
      visitor.startElement(token);
    }
  }
}
