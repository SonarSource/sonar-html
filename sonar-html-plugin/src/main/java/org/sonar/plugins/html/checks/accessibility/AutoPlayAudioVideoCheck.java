/*
 * SonarQube HTML
 * Copyright (C) 2010-2025 SonarSource SA
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
package org.sonar.plugins.html.checks.accessibility;

import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;

import java.util.Locale;

@Rule(key="S7929")
public class AutoPlayAudioVideoCheck extends AbstractPageCheck {

  @Override
  public void startElement(TagNode element) {
    String nodeName = element.getNodeName().toLowerCase(Locale.ENGLISH);

    // Only check <audio> and <video> tags
    if (!nodeName.equals("audio") && !nodeName.equals("video")) {
      return;
    }

    String autoplayAttr = element.getAttribute("autoplay");
    String mutedAttr = element.getAttribute("muted");

    // Normalize values (null-safe)
    boolean autoplay = "true".equalsIgnoreCase(autoplayAttr);
    boolean muted = "true".equalsIgnoreCase(mutedAttr);

    // Rule applicability
    if (autoplay && !muted) {
      createViolation(element,
              String.format(
                      "<%s> element plays automatically with audio and is not muted.",
                      nodeName
              )
      );
    }
  }
}