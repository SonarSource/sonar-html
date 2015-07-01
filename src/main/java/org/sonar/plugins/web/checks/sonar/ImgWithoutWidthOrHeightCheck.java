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

import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.checks.RuleTags;
import org.sonar.plugins.web.checks.WebRule;
import org.sonar.plugins.web.node.TagNode;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;

@Rule(
  key = "ImgWithoutWidthOrHeightCheck",
  priority = Priority.MAJOR,
  name = "Images tags and buttons should always have a \"width\" and a \"height\" attribute")
@WebRule(activeByDefault = false)
@RuleTags({
  RuleTags.USER_EXPERIENCE
})
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.CPU_EFFICIENCY)
@SqaleConstantRemediation("10min")
public class ImgWithoutWidthOrHeightCheck extends AbstractPageCheck {

  @Override
  public void startElement(TagNode node) {
    if ((isImgTag(node) || isImageInput(node)) && (!hasWidthAttribute(node) || !hasHeightAttribute(node))) {
      createViolation(node.getStartLinePosition(), "Add both a 'width' and a 'height' attribute to this image.");
    }
  }

  private static boolean isImgTag(TagNode node) {
    return "IMG".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isImageInput(TagNode node) {
    String type = node.getAttribute("TYPE");

    return "INPUT".equalsIgnoreCase(node.getNodeName()) &&
      type != null &&
      "IMAGE".equalsIgnoreCase(type);
  }

  private static boolean hasWidthAttribute(TagNode node) {
    return node.getAttribute("width") != null;
  }

  private static boolean hasHeightAttribute(TagNode node) {
    return node.getAttribute("height") != null;
  }

}
