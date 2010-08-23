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
