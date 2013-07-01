/*
 * Sonar Web Plugin
 * Copyright (C) 2010 SonarSource and Matthijs Galesloot
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

import net.sourceforge.pmd.cpd.TokenEntry;
import net.sourceforge.pmd.cpd.Tokens;
import org.sonar.channel.Channel;
import org.sonar.channel.CodeReader;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LiteralChannel extends Channel<Tokens> {

  private final Matcher matcher;
  private StringBuilder token = new StringBuilder();
  private final String fileName;

  public LiteralChannel(String fileName) {
    this.fileName = fileName;
    this.matcher = Pattern.compile("['\"].*?['\"]").matcher("");
  }

  @Override
  public boolean consume(CodeReader code, Tokens cpdTokens) {
    if (code.popTo(matcher, token) > 0) {
      cpdTokens.add(new TokenEntry(token.toString(), fileName, code.getLinePosition()));
      token = new StringBuilder();
      return true;
    } else {
      return false;
    }
  }
}
