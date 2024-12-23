/*
 * SonarQube HTML
 * Copyright (C) 2010-2024 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package org.sonar.plugins.html.checks.sonar;

import java.util.Set;
import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "S4084")
public class VideoTrackCheck extends AbstractPageCheck {

  private static final Set<String> ACCESSIBILITY_TRACK_KINDS = Set.of(
    "captions",
    "descriptions",
    "subtitles"
  );

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
    var kind = node.getPropertyValue("KIND");
    return node.equalsElementName("TRACK") && kind != null && ACCESSIBILITY_TRACK_KINDS.contains(kind);
  }

}
