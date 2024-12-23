/*
 * SonarQube HTML Plugin :: Sonar Plugin
 * Copyright (C) 2010-2024 SonarSource SA
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
