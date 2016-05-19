/*
 * SonarSource :: Web :: Sonar Plugin
 * Copyright (c) 2010-2016 SonarSource SA and Matthijs Galesloot
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
package org.sonar.plugins.web.duplications;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import net.sourceforge.pmd.cpd.Tokens;

import org.junit.Test;
import org.sonar.channel.CodeReader;

public class LiteralChannelTest {

  LiteralChannel channel = new LiteralChannel("fileName");
  Tokens cpdTokens = new Tokens();

  @Test
  public void testSimpleQuote() {
    CodeReader reader = new CodeReader("'literal'!");
    assertTrue(channel.consume(reader, cpdTokens));
    assertThat((char) reader.pop(), is('!'));
  }

  @Test
  public void testDoubleQuote() {
    CodeReader reader = new CodeReader("\"literal\"!");
    assertTrue(channel.consume(reader, cpdTokens));
    assertThat((char) reader.pop(), is('!'));
  }
}
