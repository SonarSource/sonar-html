/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (c) 2010-2021 SonarSource SA and Matthijs Galesloot
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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "S4084")
public class VideoTrackCheck extends AbstractPageCheck {

  private static final Set<String> ACCESSIBILITY_TRACK_KINDS = new HashSet<>(Arrays.asList(
    "captions",
    "descriptions",
    "subtitles"
  ));

  @Override
  public void startElement(TagNode node) {
    if (isVideoTag(node) && hasVideoSrc(node) && !hasAccessibilityTrackDescendant(node)) {
      createViolation(node, "Add subtitles and description files for this video.");
    }
  }

  private static boolean hasVideoSrc(TagNode node) {
    return node.hasProperty("SRC") || hasVideoSrcDescendant(node);
  }

  private static boolean hasVideoSrcDescendant(TagNode node) {
    return node.getChildren().stream().anyMatch(VideoTrackCheck::isSourceTag) ||
      node.getChildren().stream().anyMatch(VideoTrackCheck::hasVideoSrcDescendant);
  }

  private static boolean hasAccessibilityTrackDescendant(TagNode node) {
    return node.getChildren().stream().anyMatch(VideoTrackCheck::isAccessibilityTrackTag) ||
      node.getChildren().stream().anyMatch(VideoTrackCheck::hasAccessibilityTrackDescendant);
  }

  private static boolean isVideoTag(TagNode node) {
    return node.equalsElementName("VIDEO");
  }

  private static boolean isSourceTag(TagNode node) {
    return node.equalsElementName("SOURCE");
  }

  private static boolean isAccessibilityTrackTag(TagNode node) {
    return node.equalsElementName("TRACK") && ACCESSIBILITY_TRACK_KINDS.contains(node.getPropertyValue("KIND"));
  }

}
