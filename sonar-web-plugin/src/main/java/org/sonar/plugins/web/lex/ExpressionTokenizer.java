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
package org.sonar.plugins.web.lex;

import java.util.List;

import org.sonar.plugins.web.node.ExpressionNode;
import org.sonar.plugins.web.node.Node;

/**
 * Tokenizer for expressions.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
class ExpressionTokenizer extends AbstractTokenizer<List<Node>> {

  public ExpressionTokenizer(String startChars, String endChars) {
    super(startChars, endChars);
  }

  @Override
  Node createNode() {
    return new ExpressionNode();
  }

}
