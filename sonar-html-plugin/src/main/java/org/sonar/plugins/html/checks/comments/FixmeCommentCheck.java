/*
 * SonarQube HTML
 * Copyright (C) 2010-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource Sàrl.
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

@Rule(key = "S1134")
public class FixmeCommentCheck extends AbstractPageCheck {

  private static final Pattern FIXME_PATTERN = Pattern.compile("(?i)(^|[^\\p{L}])(fixme)");

  @Override
  public void comment(CommentNode node) {
    Matcher matcher = FIXME_PATTERN.matcher(node.getCode());
    if (matcher.find()) {
      int lineNumber = CommentUtils.lineNumber(node, matcher.start(2));
      createViolation(lineNumber, "Take the required action to fix the issue indicated by this \"FIXME\" comment.");
    }
  }

}
