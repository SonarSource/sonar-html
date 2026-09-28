/*
 * SonarQube HTML
 * Copyright (C) SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * You can redistribute and/or modify this program under the terms of
 * the Sonar Source-Available License Version 1, as published by SonarSource Sàrl.
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
import org.sonar.plugins.html.node.Attribute;
import org.sonar.plugins.html.node.TagNode;

import java.util.Locale;
import javax.annotation.Nullable;

@Rule(key="S7929")
public class AutoPlayAudioVideoCheck extends AbstractPageCheck {

  @Override
  public void startElement(TagNode element) {
    String nodeName = element.getNodeName().toLowerCase(Locale.ENGLISH);

    // Only check <audio> and <video> tags
    if (!nodeName.equals("audio") && !nodeName.equals("video")) {
      return;
    }

    // Normalize values (null-safe)
    boolean autoplay = "true".equalsIgnoreCase(valueOf(element.getStaticProperty("autoplay")));
    boolean muted = "true".equalsIgnoreCase(valueOf(element.getStaticProperty("muted")));

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

  @Nullable
  private static String valueOf(@Nullable Attribute attribute) {
    return attribute == null ? null : attribute.getValue();
  }
}
