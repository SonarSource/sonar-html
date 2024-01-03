/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (c) 2010-2024 SonarSource SA and Matthijs Galesloot
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

import java.util.Locale;
import java.util.Set;
import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "LinkToImageCheck")
public class LinkToImageCheck extends AbstractPageCheck {

  private static final Set<String> IMG_SUFFIXES = Set.of(".GIF", ".JPG", ".JPEG", ".PNG", ".BMP");

  @Override
  public void startElement(TagNode node) {
    if (isATag(node) && hasHrefToImage(node)) {
      createViolation(node, "Change this link to not directly target an image.");
    }
  }

  private static boolean isATag(TagNode node) {
    return "A".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean hasHrefToImage(TagNode node) {
    String href = node.getAttribute("href");

    return href != null &&
      isPointingToAnImage(href);
  }

  private static boolean isPointingToAnImage(String target) {
    final String upperTarget = target.toUpperCase(Locale.ENGLISH);
    return !upperTarget.contains("?") && IMG_SUFFIXES.stream().anyMatch(input -> input != null && upperTarget.endsWith(input));
  }

}
