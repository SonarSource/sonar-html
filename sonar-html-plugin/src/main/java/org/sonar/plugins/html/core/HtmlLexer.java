/*
 * SonarQube HTML
 * Copyright (C) 2010-2025 SonarSource Sàrl
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

import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.impl.Lexer;
import com.sonar.sslr.impl.channel.BlackHoleChannel;
import com.sonar.sslr.impl.channel.BomCharacterChannel;
import com.sonar.sslr.impl.channel.IdentifierAndKeywordChannel;
import com.sonar.sslr.impl.channel.UnknownCharacterChannel;

import java.nio.charset.Charset;

import static com.sonar.sslr.impl.channel.RegexpChannelBuilder.commentRegexp;
import static com.sonar.sslr.impl.channel.RegexpChannelBuilder.regexp;

final class HtmlLexer {

  private HtmlLexer() {
  }

  public static Lexer create(Charset charset) {
    return Lexer.builder()
      .withCharset(charset)

      .withFailIfNoChannelToConsumeOneCharacter(true)

      .withChannel(new BomCharacterChannel())
      .withChannel(new BlackHoleChannel("\\s++"))

      .withChannel(regexp(HtmlTokenType.DOCTYPE, "<!DOCTYPE.*>"))

      .withChannel(regexp(HtmlTokenType.TAG, "</?[:\\w]+>?"))
      .withChannel(regexp(HtmlTokenType.TAG, "/?>"))

      // JSP comment
      .withChannel(commentRegexp("<%--[\\w\\W]*?%>"))
      // HTML comment
      .withChannel(commentRegexp("<!--[\\w\\W]*?-->"))
      // C comment
      .withChannel(commentRegexp("/\\*[\\w\\W]*?\\*/"))
      // CPP comment
      .withChannel(commentRegexp("//[^\n\r]*"))

      .withChannel(regexp(HtmlTokenType.EXPRESSION, "<%[\\w\\W]*?%>"))

      .withChannel(regexp(HtmlTokenType.ATTRIBUTE, "=[\"']{1}[\\w\\W]*?[\"']{1}"))
      .withChannel(regexp(HtmlTokenType.ATTRIBUTE, "=[^\\s'\"=<>`]++"))

      .withChannel(new IdentifierAndKeywordChannel("\\w++", true, new TokenType[]{}))

      .withChannel(new UnknownCharacterChannel())

      .build();
  }

}
