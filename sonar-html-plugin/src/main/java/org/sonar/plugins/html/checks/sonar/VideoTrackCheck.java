/*
 * SonarHTML :: SonarQube Plugin
 * Copyright (c) 2010-2018 SonarSource SA and Matthijs Galesloot
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
import org.sonar.plugins.html.node.Attribute;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "S4084")
public class VideoTrackCheck extends AbstractPageCheck {

  @Override
  public void startElement(TagNode node) {
    if (isVideoTag(node) && hasVideoSrc(node) && !hasTrackChild(node)) {
      createViolation(node.getStartLinePosition(), "Add subtitle files for this video.");
    }
  }

  private static boolean hasVideoSrc(TagNode node) {
    return node.getAttributes().stream().anyMatch(VideoTrackCheck::isSrcAttribute) ||
      node.getChildren().stream().anyMatch(VideoTrackCheck::isSourceTag);
  }

  private static boolean hasTrackChild(TagNode node) {
    return node.getChildren().stream().anyMatch(VideoTrackCheck::isTrackTag);
  }

  private static boolean isVideoTag(TagNode node) {
    return node.equalsElementName("VIDEO");
  }

  private static boolean isSourceTag(TagNode node) {
    return node.equalsElementName("SOURCE");
  }

  private static boolean isSrcAttribute(Attribute attribute) {
    return "SRC".equalsIgnoreCase(attribute.getName());
  }

  private static boolean isTrackTag(TagNode node) {
    return node.equalsElementName("TRACK");
  }

}
