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
package org.sonar.plugins.html.checks.sonar;

import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "BoldAndItalicTagsCheck")
public class BoldAndItalicTagsCheck extends AbstractPageCheck {

  @Override
  public void startElement(TagNode node) {
    if (isBold(node)) {
      createViolation(node, "Replace this <" + node.getNodeName() + "> tag by <strong>.");
    } else if (isItalicAndNotAriaHidden(node)) {
      createViolation(node, "Replace this <" + node.getNodeName() + "> tag by <em>.");
    }
  }

  private static boolean isBold(TagNode node) {
    return "B".equalsIgnoreCase(node.getNodeName());
  }

  /**
   * Check if node is an italic tag and attribute 'aria-hidden' is not true.
   * Rule should be relaxed in this case ; <a href="https://www.w3.org/WAI/GL/wiki/Using_aria-hidden%3Dtrue_on_an_icon_font_that_AT_should_ignore">icon font</a> usage.
   * @param node The current HTML start tag
   * @return true if violation should be applied, false otherwise
   */
  private static boolean isItalicAndNotAriaHidden(TagNode node) {
    return "I".equalsIgnoreCase(node.getNodeName()) && !"true".equalsIgnoreCase(node.getPropertyValue("aria-hidden"));
  }

}
