/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (C) 2010-2024 SonarSource SA and Matthijs Galesloot
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package org.sonar.plugins.html.core;

import com.sonar.sslr.impl.Lexer;
import com.sonar.sslr.impl.channel.BomCharacterChannel;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.sonar.sslr.test.lexer.LexerMatchers.hasComment;
import static com.sonar.sslr.test.lexer.LexerMatchers.hasToken;
import static com.sonar.sslr.test.lexer.LexerMatchers.hasTokens;
import static org.hamcrest.MatcherAssert.assertThat;

public class HtmlLexerTest {
  private static Lexer lexer;

  @BeforeAll
  public static void init() {
    lexer = HtmlLexer.create(StandardCharsets.UTF_8);
  }

  @Test
  public void tags() throws Exception {
    assertThat("<a>", lexer.lex("<a>"), hasToken("<a>", HtmlTokenType.TAG));
    assertThat("</a>", lexer.lex("</a>"), hasToken("</a>", HtmlTokenType.TAG));
    assertThat("<a href=\"uri\">", lexer.lex("<a href=\"uri\">"), hasToken("<a", HtmlTokenType.TAG));
    assertThat("<img src=\"uri\" />", lexer.lex("<img src=\"uri\" />"), hasToken("<img", HtmlTokenType.TAG));
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
    assertThat("<a href=\"uri\">", lexer.lex("<a href=\"uri\">"), hasToken("=\"uri\"", HtmlTokenType.ATTRIBUTE));
    assertThat("<a src=uri/a />", lexer.lex("<a src=uri/a />"), hasToken("=uri/a", HtmlTokenType.ATTRIBUTE));
  }

  @Test
  public void bom() {
    assertThat(lexer.lex(Character.toString((char) BomCharacterChannel.BOM_CHAR)), hasTokens("EOF"));
  }

}
