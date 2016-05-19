/*
 * SonarSource :: Web :: Sonar Plugin
 * Copyright (c) 2010-2016 SonarSource SA and Matthijs Galesloot
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

import java.util.List;

import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.checks.RuleTags;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.node.TagNode;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;

@Rule(
  key = "FlashUsesBothObjectAndEmbedCheck",
  name = "Flash animations should be embedded using both the <object> and <embed> tags",
  priority = Priority.MAJOR,
  tags = RuleTags.CROSS_BROWSER)
@ActivatedByDefault
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.SOFTWARE_RELATED_PORTABILITY)
@SqaleConstantRemediation("10min")
public class FlashUsesBothObjectAndEmbedCheck extends AbstractPageCheck {

  private int objectLine;
  private boolean foundEmbed;

  @Override
  public void startDocument(List<Node> nodes) {
    objectLine = 0;
  }

  @Override
  public void startElement(TagNode node) {
    if (isObject(node) && FlashHelper.isFlashObject(node)) {
      objectLine = node.getStartLinePosition();
      foundEmbed = false;
    } else if (isEmbed(node) && FlashHelper.isFlashEmbed(node)) {
      foundEmbed = true;

      if (node.getParent() == null || !isObject(node.getParent())) {
        createViolation(node.getStartLinePosition(), "Surround this <embed> tag by an <object> one.");
      }
    }
  }

  @Override
  public void endElement(TagNode node) {
    if (isObject(node)) {
      if (objectLine != 0 && !foundEmbed) {
        createViolation(objectLine, "Add an <embed> tag within this <object> one.");
      }
      objectLine = 0;
    }
  }

  private static boolean isObject(TagNode node) {
    return "OBJECT".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isEmbed(TagNode node) {
    return "EMBED".equalsIgnoreCase(node.getNodeName());
  }

}
