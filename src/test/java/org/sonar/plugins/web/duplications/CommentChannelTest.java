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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sonar.channel.CodeReader;

public class CommentChannelTest {

  @Test
  public void testHtmlComment() {
    CommentChannel channel = CommentChannel.HTML_COMMENT;
    CodeReader reader = new CodeReader("<!-- My Comment -->a");
    assertTrue(channel.consume(reader, null));
    assertThat((char) reader.peek(), is('a'));
  }

  @Test
  public void testJspComment() {
    CommentChannel channel = CommentChannel.JSP_COMMENT;
    CodeReader reader = new CodeReader("<%-- My Comment --%>a");
    assertTrue(channel.consume(reader, null));
    assertThat((char) reader.peek(), is('a'));
    
    reader = new CodeReader("/* My Comment */a");
    assertFalse(channel.consume(reader, null));
  }

  @Test
  public void testCppComment() {
    CommentChannel channel = CommentChannel.CPP_COMMENT;
    CodeReader reader = new CodeReader("// My Comment \n\r");
    assertTrue(channel.consume(reader, null));
    assertThat((char) reader.peek(), is('\n'));
  }

  @Test
  public void testCComment() {
    CommentChannel channel = CommentChannel.C_COMMENT;
    CodeReader reader = new CodeReader("/* My Comment \n second line */a");
    assertTrue(channel.consume(reader, null));
    assertThat((char) reader.peek(), is('a'));
    
    reader = new CodeReader(" My Comment */a");
    assertFalse(channel.consume(reader, null));
  }
}
