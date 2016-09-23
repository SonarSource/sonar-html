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

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.checks.RuleTags;
import org.sonar.plugins.web.node.TagNode;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;

@Rule(
  key = "ImgWithoutWidthOrHeightCheck",
  name = "Images tags and buttons should have a \"width\" and a \"height\" attribute",
  priority = Priority.MAJOR,
  tags = {RuleTags.USER_EXPERIENCE})
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
