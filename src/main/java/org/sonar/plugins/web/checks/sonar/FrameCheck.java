/*
 * SonarQube Web Plugin
 * Copyright (C) 2010 SonarSource and Matthijs Galesloot
 * dev@sonar.codehaus.org
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

import com.google.common.collect.ImmutableSet;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.checks.RuleTags;
import org.sonar.plugins.web.checks.WebRule;
import org.sonar.plugins.web.node.TagNode;

import java.util.Locale;
import java.util.Set;

@Rule(
  key = "S1826",
  priority = Priority.MAJOR)
@WebRule(activeByDefault = true)
@RuleTags({
  RuleTags.HTML5,
  RuleTags.OBSOLETE,
  RuleTags.USER_EXPERIENCE
})
public class FrameCheck extends AbstractPageCheck {

  private static final Set<String> FRAMES_TAGS = ImmutableSet.of(
      "FRAME",
      "FRAMESET",
      "NOFRAMES");

  @Override
  public void startElement(TagNode node) {
    if (FRAMES_TAGS.contains(node.getNodeName().toUpperCase(Locale.ENGLISH))) {
      createViolation(node.getStartLinePosition(), "Remove this \"" + node.getNodeName() + "\" frame.");
    }
  }

}
