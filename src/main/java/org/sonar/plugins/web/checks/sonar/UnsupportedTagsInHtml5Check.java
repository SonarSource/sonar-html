/*
 * Sonar Web Plugin
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
import org.sonar.plugins.web.node.TagNode;

import java.util.Locale;
import java.util.Set;

@Rule(
  key = "UnsupportedTagsInHtml5Check",
  priority = Priority.MAJOR)
public class UnsupportedTagsInHtml5Check extends AbstractPageCheck {

  private static final Set<String> UNSUPPORTED_TAGS = ImmutableSet.of(
      "ACRONYM",
      "APPLET",
      "BASEFONT",
      "BIG",
      "CENTER",
      "DIR",
      "FONT",
      "FRAME",
      "FRAMESET",
      "ISINDEX",
      "NOFRAMES",
      "STRIKE",
      "TT");

  @Override
  public void startElement(TagNode node) {
    if (isUnsupportedTag(node)) {
      createViolation(node.getStartLinePosition(), "Remove usage of this '" + node.getNodeName() + "' tag.");
    }
  }

  private static boolean isUnsupportedTag(TagNode node) {
    return UNSUPPORTED_TAGS.contains(node.getNodeName().toUpperCase(Locale.ENGLISH));
  }

}
