/*
 * SonarQube Web Plugin
 * Copyright (C) 2010 SonarSource and Matthijs Galesloot
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
package org.sonar.plugins.web.checks.sonar;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.checks.RuleTags;
import org.sonar.plugins.web.node.TagNode;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.NoSqale;

@Rule(
  key = "BoldAndItalicTagsCheck",
  name = "<strong> and <em> tags should be used instead of <b> and <i>",
  priority = Priority.MAJOR,
  tags = {RuleTags.ACCESSIBILITY})
@ActivatedByDefault
@NoSqale
public class BoldAndItalicTagsCheck extends AbstractPageCheck {

  @Override
  public void startElement(TagNode node) {
    if (isBold(node)) {
      createViolation(node.getStartLinePosition(), "Replace this <" + node.getNodeName() + "> tag by <strong>.");
    } else if (isItalic(node)) {
      createViolation(node.getStartLinePosition(), "Replace this <" + node.getNodeName() + "> tag by <em>.");
    }
  }

  private static boolean isBold(TagNode node) {
    return "B".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isItalic(TagNode node) {
    return "I".equalsIgnoreCase(node.getNodeName());
  }

}
