/*
 * SonarQube HTML
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.CommentNode;

@Rule(key = "S1135")
public class TodoCommentCheck extends AbstractPageCheck {

  private static final Pattern TODO_PATTERN = Pattern.compile("(?i)(^|[^\\p{L}])(todo)");

  @Override
  public void comment(CommentNode node) {
    Matcher matcher = TODO_PATTERN.matcher(node.getCode());
    if (matcher.find()) {
      int lineNumber = CommentUtils.lineNumber(node, matcher.start(2));
      createViolation(lineNumber, "Complete the task associated to this \"TODO\" comment.");
    }
  }

}
