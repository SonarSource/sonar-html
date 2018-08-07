/*
 * SonarHTML :: SonarQube Plugin
 * Copyright (c) 2010-2018 SonarSource SA and Matthijs Galesloot
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
package org.sonar.plugins.html.checks.comments;

import org.sonar.plugins.html.node.CommentNode;

final class CommentUtils {

  private CommentUtils() {
    // utlility
  }

  static int lineNumber(CommentNode node, int offset) {
    String content = node.getCode();
    if (offset < 0 || offset > content.length()) {
      throw new IllegalArgumentException("Out of range offset: " + offset + " for comment content (size: " + content.length() + ")");
    }
    int lineFeedCountBeforeOffset = (int) content.substring(0, offset).chars().filter(c -> c == '\n').count();
    return node.getStartLinePosition() + lineFeedCountBeforeOffset;
  }

}
