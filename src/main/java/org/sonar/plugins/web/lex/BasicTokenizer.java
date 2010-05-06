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

import org.apache.commons.lang.ArrayUtils;
import org.sonar.channel.Channel;
import org.sonar.channel.CodeReader;
import org.sonar.channel.EndMatcher;

/**
 * @author Matthijs Galesloot
 */
class BasicTokenizer implements Channel<HtmlLexer> {

  private char[] startToken;
  private char[] endToken;
  private Class<? extends Token> clazz;

  private Token createToken() {
    try {
      return clazz.newInstance();
    } catch (InstantiationException e) {
      return new Token();
    } catch (IllegalAccessException e) {
      return new Token();
    }
  }

  public BasicTokenizer(Class<? extends Token> clazz, String startToken, String endToken) {
    this.clazz = clazz;
    this.startToken = startToken.toCharArray();
    this.endToken = endToken.toCharArray();
  }

  private class EndTokenMatcher implements EndMatcher {

    private CodeReader codeReader;

    private EndTokenMatcher(CodeReader codeReader) {
      this.codeReader = codeReader;
    }

    public boolean match(int endFlag) {
      return endFlag == endToken[0] && ArrayUtils.isEquals(codeReader.peek(endToken.length), endToken);
    }
  };

  public boolean consum(CodeReader codeReader, HtmlLexer lexer) {
    if (ArrayUtils.isEquals(codeReader.peek(startToken.length), startToken)) {
      Token token = createToken();
      token.setStartPosition(codeReader);

      StringBuilder stringBuilder = new StringBuilder();
      codeReader.popTo(new EndTokenMatcher(codeReader), stringBuilder);
      for (int i = 0; i < endToken.length; i++) {
        codeReader.pop(stringBuilder);
      }
      token.setCode(stringBuilder.toString());
      token.setEndPosition(codeReader);

      lexer.produce(token);
      return true;
    } else {
      return false;
    }
  }
}
