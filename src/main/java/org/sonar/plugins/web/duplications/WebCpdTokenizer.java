/*
 * Sonar Web Plugin
 * Copyright (C) 2010 Matthijs Galesloot
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
package org.sonar.plugins.web.duplications;

import net.sourceforge.pmd.cpd.SourceCode;
import net.sourceforge.pmd.cpd.TokenEntry;
import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.cpd.Tokens;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.channel.ChannelDispatcher;
import org.sonar.channel.CodeReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class WebCpdTokenizer implements Tokenizer {

  public static final Logger LOG = LoggerFactory.getLogger(WebCpdTokenizer.class.getName());

  public final void tokenize(SourceCode source, Tokens cpdTokens) {
    String fileName = source.getFileName();

    ChannelDispatcher.Builder lexerBuilder = ChannelDispatcher.builder();
    lexerBuilder.addChannel(CommentChannel.JSP_COMMENT);
    lexerBuilder.addChannel(CommentChannel.HTML_COMMENT);
    lexerBuilder.addChannel(CommentChannel.C_COMMENT);
    lexerBuilder.addChannel(CommentChannel.CPP_COMMENT);
    lexerBuilder.addChannel(new WordChannel(fileName));
    lexerBuilder.addChannel(new LiteralChannel(fileName));
    lexerBuilder.addChannel(new BlackHoleChannel());
    ChannelDispatcher<Tokens> lexer = lexerBuilder.build();

    try {
      lexer.consume(new CodeReader(new FileReader(new File(fileName))), cpdTokens);
      cpdTokens.add(TokenEntry.getEOF());
    } catch (FileNotFoundException e) {
      LOG.error("Unable to open file : " + fileName, e);
    }
  }
}
