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
package org.sonar.plugins.web.checks.comments;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.checks.RuleTags;
import org.sonar.plugins.web.checks.WebRule;
import org.sonar.plugins.web.node.CommentNode;
import org.sonar.squid.recognizer.CodeRecognizer;
import org.sonar.squid.recognizer.ContainsDetector;
import org.sonar.squid.recognizer.Detector;
import org.sonar.squid.recognizer.EndWithDetector;
import org.sonar.squid.recognizer.LanguageFootprint;

import com.google.common.collect.ImmutableSet;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;

@Rule(
  key = "AvoidCommentedOutCodeCheck",
  priority = Priority.MAJOR,
  name = "Sections of code should not be \"commented out\"")
@WebRule(activeByDefault = true)
@RuleTags({
  RuleTags.UNUSED
})
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.UNDERSTANDABILITY)
@SqaleConstantRemediation("5min")
public class AvoidCommentedOutCodeCheck extends AbstractPageCheck {

  private static final double THRESHOLD = 0.9;

  private static final LanguageFootprint LANGUAGE_FOOTPRINT = new LanguageFootprint() {

    @Override
    public Set<Detector> getDetectors() {
      return ImmutableSet.of(
        new ContainsDetector(0.7, "=\"", "='"),
        new ContainsDetector(0.8, "/>", "</", "<%", "%>"),
        new EndWithDetector(0.9, '>'));
    }

  };

  private static final CodeRecognizer CODE_RECOGNIZER = new CodeRecognizer(THRESHOLD, LANGUAGE_FOOTPRINT);

  @Override
  public void comment(CommentNode node) {
    if (node.isHtml()) {
      String comment = node.getCode();

      if (!comment.startsWith("<!--[if") && !StringUtils.containsIgnoreCase(comment, "copyright") && CODE_RECOGNIZER.isLineOfCode(comment)) {
        createViolation(node.getStartLinePosition(), "Remove this commented out code.");
      }
    }
  }

}
