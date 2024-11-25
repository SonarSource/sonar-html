/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (C) 2010-2024 SonarSource SA and Matthijs Galesloot
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

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.CommentNode;
import org.sonarsource.analyzer.commons.recognizers.CodeRecognizer;
import org.sonarsource.analyzer.commons.recognizers.ContainsDetector;
import org.sonarsource.analyzer.commons.recognizers.EndWithDetector;
import org.sonarsource.analyzer.commons.recognizers.LanguageFootprint;

@Rule(key = "AvoidCommentedOutCodeCheck")
public class AvoidCommentedOutCodeCheck extends AbstractPageCheck {

  private static final Pattern COPYRIGHT_CASE_INSENSITIVE = Pattern.compile("copyright", Pattern.CASE_INSENSITIVE);
  private static final double THRESHOLD = 0.9;

  private static final LanguageFootprint LANGUAGE_FOOTPRINT = () -> Set.of(
    new ContainsDetector(0.7, "=\"", "='"),
    new ContainsDetector(0.8, "/>", "</", "<%", "%>"),
    new EndWithDetector(0.9, '>'));

  private static final CodeRecognizer CODE_RECOGNIZER = new CodeRecognizer(THRESHOLD, LANGUAGE_FOOTPRINT);

  private static final List<String> IGNORED_COMMENT_ANNOTATIONS = List.of("@thymesVar", "@elvariable");

  @Override
  public void comment(CommentNode node) {
    if (node.isHtml()) {
      String comment = node.getCode();

      if (!isIgnored(comment) && CODE_RECOGNIZER.isLineOfCode(comment)) {
        createViolation(node.getStartLinePosition(), "Remove this commented out code.");
      }
    }
  }

  private static boolean isIgnored(String comment) {
    return COPYRIGHT_CASE_INSENSITIVE.matcher(comment).find()
      // Conditional comments
      || comment.startsWith("<!--[if")
      // Server Side Includes
      || comment.startsWith("<!--#")
      // Annotated comments
      || IGNORED_COMMENT_ANNOTATIONS.stream().anyMatch(comment::contains);
  }

}
