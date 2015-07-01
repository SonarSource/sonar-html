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

import java.util.Locale;
import java.util.Set;

import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.checks.RuleTags;
import org.sonar.plugins.web.checks.WebRule;
import org.sonar.plugins.web.node.TagNode;

import com.google.common.collect.ImmutableSet;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;

/** RSPEC-1083 */
@Rule(
  key = "UnsupportedTagsInHtml5Check",
  priority = Priority.MAJOR,
  name = "Elements deprecated in HTML5 should not be used")
@WebRule(activeByDefault = true)
@RuleTags({
  RuleTags.HTML5,
  RuleTags.OBSOLETE
})
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.LANGUAGE_RELATED_PORTABILITY)
@SqaleConstantRemediation("20min")
public class UnsupportedTagsInHtml5Check extends AbstractPageCheck {

  private static final Set<String> UNSUPPORTED_TAGS = ImmutableSet.of(
      "ACRONYM",
      "APPLET",
      "BASEFONT",
      "BGSOUND",
      "BIG",
      "BLINK",
      "CENTER",
      "DIR",
      "FONT",
      "FRAME",
      "FRAMESET",
      "HGROUP",
      "ISINDEX",
      "LISTING",
      "MARQUEE",
      "MULTICOL",
      "NEXTID",
      "NOBR",
      "NOEMBED",
      "NOFRAMES",
      "PLAINTEXT",
      "SPACER",
      "STRIKE",
      "TT",
      "XMP");

  @Override
  public void startElement(TagNode node) {
    if (isUnsupportedTag(node)) {
      createViolation(node.getStartLinePosition(), "Remove this deprecated \"" + node.getNodeName() + "\" element.");
    }
  }

  private static boolean isUnsupportedTag(TagNode node) {
    return UNSUPPORTED_TAGS.contains(node.getNodeName().toUpperCase(Locale.ENGLISH));
  }

}
