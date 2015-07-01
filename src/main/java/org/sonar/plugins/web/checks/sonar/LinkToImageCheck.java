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

import java.util.Locale;
import java.util.Set;

import javax.annotation.Nullable;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.checks.RuleTags;
import org.sonar.plugins.web.checks.WebRule;
import org.sonar.plugins.web.node.TagNode;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

@Rule(
  key = "LinkToImageCheck",
  priority = Priority.MAJOR,
  name = "Links should not directly target images")
@WebRule(activeByDefault = true)
@RuleTags({
  RuleTags.ACCESSIBILITY,
  RuleTags.USER_EXPERIENCE
})
public class LinkToImageCheck extends AbstractPageCheck {

  private static Set<String> IMG_SUFFIXES = ImmutableSet.of(".GIF", ".JPG", ".JPEG", ".PNG", ".BMP");

  @Override
  public void startElement(TagNode node) {
    if (isATag(node) && hasHrefToImage(node)) {
      createViolation(node.getStartLinePosition(), "Change this link to not directly target an image.");
    }
  }

  private static boolean isATag(TagNode node) {
    return "A".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean hasHrefToImage(TagNode node) {
    String href = node.getAttribute("href");

    return href != null &&
      isPoitingToAnImage(href);
  }

  private static boolean isPoitingToAnImage(String target) {
    final String upperTarget = target.toUpperCase(Locale.ENGLISH);
    return Iterables.any(IMG_SUFFIXES, new Predicate<String>() {
      @Override
      public boolean apply(@Nullable String input) {
        return input != null &&  upperTarget.endsWith(input);
      }
    });
  }

}
