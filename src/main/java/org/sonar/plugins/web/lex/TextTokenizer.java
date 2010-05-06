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

import org.sonar.channel.Channel;
import org.sonar.channel.CodeReader;
import org.sonar.channel.EndMatcher;

/**
 * @author Matthijs Galesloot
 */
class TextTokenizer implements Channel<HtmlLexer> {

  private EndMatcher endTokenMatcher = new EndMatcher() {

    public boolean match(int endFlag) {
      return endFlag == '<';
    }
  };

  public TextTokenizer() {
  }

  public boolean consum(CodeReader code, HtmlLexer lexer) {
    Token token = new Token();
    token.setStartPosition(code);

    StringBuilder stringBuilder = new StringBuilder();
    code.popTo(endTokenMatcher, stringBuilder);
    token.setCode(stringBuilder.toString());
    token.setEndPosition(code);

    lexer.produce(token);

    return true;
  }
}
