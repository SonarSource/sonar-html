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

import org.sonar.plugins.html.api.accessibility.AriaProperty;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;

import static org.sonar.plugins.html.api.HtmlConstants.isReservedNode;

import java.util.Locale;

import org.sonar.check.Rule;

@Rule(key = "S6824")
public class AriaUnsupportedElementsCheck extends AbstractPageCheck {
  @Override
  public void startElement(TagNode element) {
    // Following logic from:
    // https://github.com/jsx-eslint/eslint-plugin-jsx-a11y/blob/main/src/rules/aria-unsupported-elements.js
    if (!isReservedNode(element)) {
      return;
    }
    element.getAttributes().forEach(attr -> {
      var attrName = attr.getName().toLowerCase(Locale.ROOT);
      if (AriaProperty.of(attrName) != null || attrName.equals("role")) {
        createViolation(
            element,
            String.format("This element does not support ARIA roles, states and properties. Try removing the prop %s.",
                attr.getName()));
      }
    });
  }
}
