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

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.pmd.cpd.Tokens;

import org.sonar.channel.Channel;
import org.sonar.channel.CodeReader;

public class CommentChannel extends Channel<Tokens> {

  private final Matcher matcher;
  private final EmptyAppendable appendable = new EmptyAppendable();

  public static final CommentChannel HTML_COMMENT = new CommentChannel("<!--[\\w\\W]*?-->");
  public static final CommentChannel JSP_COMMENT = new CommentChannel("<%--[\\w\\W]*?%>");
  public static final CommentChannel CPP_COMMENT = new CommentChannel("//[^\n\r]*");
  public static final CommentChannel C_COMMENT = new CommentChannel("/\\*[\\w\\W]*?\\*/");

  public CommentChannel(String regex) {
    this.matcher = Pattern.compile(regex).matcher("");
  }

  @Override
  public boolean consume(CodeReader code, Tokens cpdTokens) {
    return code.popTo(matcher, appendable) > 0;
  }

  private static class EmptyAppendable implements Appendable {

    public Appendable append(CharSequence csq) throws IOException {
      return null;
    }

    public Appendable append(char c) throws IOException {
      return null;
    }

    public Appendable append(CharSequence csq, int start, int end) throws IOException {
      return null;
    }
  }
}
