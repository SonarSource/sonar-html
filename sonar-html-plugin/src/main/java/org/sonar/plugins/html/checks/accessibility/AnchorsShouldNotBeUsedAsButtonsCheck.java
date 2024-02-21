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

import java.util.Objects;
import java.util.regex.Pattern;

@Rule(key = "S6844")
public class AnchorsShouldNotBeUsedAsButtonsCheck extends AbstractPageCheck {
  static boolean isAJavascriptHandler(String value) {
    String pattern = "^\\W*?javascript:.*";

    return Pattern.matches(pattern, value);
  }

  @Override
  public void startElement(TagNode node) {
    if (Objects.equals(node.getNodeName().toLowerCase(), "a")) {
      String onClickAttribute = node.getAttribute("onclick");

      if (onClickAttribute != null) {
        String hrefAttribute = node.getAttribute("href");

        if (hrefAttribute == null || hrefAttribute.isBlank() || hrefAttribute.equals("#") || isAJavascriptHandler(hrefAttribute)) {
          createViolation(node, "Anchor tags should not be used as buttons.");
        }
      }
    }
  }
}
