/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (c) 2010-2024 SonarSource SA and Matthijs Galesloot
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
package org.sonar.plugins.html.checks.accessibility;

import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static org.sonar.plugins.html.checks.accessibility.AccessibilityUtils.isHiddenFromScreenReader;

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

    var words = REDUNDANT_WORDS.stream().filter(w -> alt.toLowerCase(Locale.ENGLISH).contains(w)).collect(Collectors.toList());
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
