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
package org.sonar.plugins.html.checks.accessibility;

import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static org.sonar.plugins.html.api.accessibility.AccessibilityUtils.isHiddenFromScreenReader;

@Rule(key = "S6851")
public class ImgRedundantAltCheck extends AbstractPageCheck {

  private static final String MESSAGE_TEMPLATE = "Remove redundant word%s %s from the \"alt\" attribute of your \"img\" tag.";
  private static final List<String> REDUNDANT_WORDS = List.of("image", "photo", "picture");

  @Override
  public void startElement(TagNode element) {
    if (!isImg(element) || isHiddenFromScreenReader(element)) {
      return;
    }

    var alt = element.getPropertyValue("alt");
    if (alt == null) {
      return;
    }

    var words = REDUNDANT_WORDS.stream().filter(w -> alt.toLowerCase(Locale.ENGLISH).contains(w)).toList();
    if (!words.isEmpty()) {
      var quotedWords = words.stream().map(w -> "\"" + w + "\"" ).collect(Collectors.joining(", "));
      var punctuation = words.size() == 1 ? "" : "s";
      var message = String.format(MESSAGE_TEMPLATE, punctuation, quotedWords);
      createViolation(element.getStartLinePosition(), message);
    }
  }

  private static boolean isImg(TagNode element) {
    return "img".equalsIgnoreCase(element.getNodeName());
  }
}
