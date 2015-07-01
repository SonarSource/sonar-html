/*
 * SonarQube Web Plugin
 * Copyright (C) 2010 SonarSource and Matthijs Galesloot
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

import net.sourceforge.pmd.cpd.Tokens;
import org.sonar.channel.Channel;
import org.sonar.channel.CodeReader;

public class BlackHoleChannel extends Channel<Tokens> {

  @Override
  public boolean consume(CodeReader code, Tokens cpdTokens) {
    code.pop();
    return true;
  }

}
