/*
 * SonarSource :: Web :: Sonar Plugin
 * Copyright (c) 2010-2017 SonarSource SA and Matthijs Galesloot
 * sonarqube@googlegroups.com
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
package org.sonar.plugins.web.core;

import com.sonar.sslr.impl.Lexer;
import com.sonar.sslr.impl.channel.BomCharacterChannel;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static com.sonar.sslr.test.lexer.LexerMatchers.hasComment;
import static com.sonar.sslr.test.lexer.LexerMatchers.hasToken;
import static com.sonar.sslr.test.lexer.LexerMatchers.hasTokens;
import static org.junit.Assert.assertThat;

public class WebLexerTest {
  private static Lexer lexer;

  @BeforeClass
  public static void init() {
    lexer = WebLexer.create(StandardCharsets.UTF_8);
  }

  @Test
  public void tags() throws Exception {
    assertThat("<a>", lexer.lex("<a>"), hasToken("<a>", WebTokenType.TAG));
    assertThat("</a>", lexer.lex("</a>"), hasToken("</a>", WebTokenType.TAG));
    assertThat("<a href=\"uri\">", lexer.lex("<a href=\"uri\">"), hasToken("<a", WebTokenType.TAG));
    assertThat("<img src=\"uri\" />", lexer.lex("<img src=\"uri\" />"), hasToken("<img", WebTokenType.TAG));
  }

  @Test
  public void multiline_comment() {
    assertThat(lexer.lex("<!-- My Comment \n a -->"), hasComment("<!-- My Comment \n a -->"));
    assertThat(lexer.lex("<%-- My Comment \n a %>"), hasComment("<%-- My Comment \n a %>"));
    assertThat(lexer.lex("/* My Comment \n a */"), hasComment("/* My Comment \n a */"));
  }

  @Test
  public void inline_comment() {
    assertThat(lexer.lex("// My Comment \n new line"), hasComment("// My Comment "));
    assertThat(lexer.lex("//"), hasComment("//"));
  }

  @Test
  public void attribute() {
    assertThat("<a href=\"uri\">", lexer.lex("<a href=\"uri\">"), hasToken("=\"uri\"", WebTokenType.ATTRIBUTE));
    assertThat("<a src=uri/a />", lexer.lex("<a src=uri/a />"), hasToken("=uri/a", WebTokenType.ATTRIBUTE));
  }

  @Test
  public void bom() {
    assertThat(lexer.lex(Character.toString((char) BomCharacterChannel.BOM_CHAR)), hasTokens("EOF"));
  }

}
