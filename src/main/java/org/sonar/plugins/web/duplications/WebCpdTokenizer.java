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

package org.sonar.plugins.web.duplications;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.cpd.SourceCode;
import net.sourceforge.pmd.cpd.TokenEntry;
import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.cpd.Tokens;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.channel.Channel;
import org.sonar.channel.ChannelDispatcher;
import org.sonar.channel.CodeReader;

public class WebCpdTokenizer implements Tokenizer {

  public static final Logger LOG = LoggerFactory.getLogger(WebCpdTokenizer.class.getName());

  public final void tokenize(SourceCode source, Tokens cpdTokens) {
    String fileName = source.getFileName();

    List<Channel> channels = new ArrayList<Channel>();
    channels.add(CommentChannel.JSP_COMMENT);
    channels.add(CommentChannel.HTML_COMMENT);
    channels.add(CommentChannel.C_COMMENT);
    channels.add(CommentChannel.CPP_COMMENT);
    channels.add(new WordChannel(fileName));
    channels.add(new LiteralChannel(fileName));
    channels.add(new BlackHoleChannel());

    ChannelDispatcher<Tokens> lexer = new ChannelDispatcher<Tokens>(channels);

    try {
      lexer.consume(new CodeReader(new FileReader(new File(fileName))), cpdTokens);
      cpdTokens.add(TokenEntry.getEOF());
    } catch (FileNotFoundException e) {
      LOG.error("Unable to open file : " + fileName, e);
    }
  }
}
