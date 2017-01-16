/*
 * SonarSource :: Web :: Sonar Plugin
 * Copyright (c) 2010-2017 SonarSource SA and Matthijs Galesloot
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

import org.sonar.check.Rule;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.node.Attribute;
import org.sonar.plugins.web.node.TagNode;

@Rule(key = "WmodeIsWindowCheck")
public class WmodeIsWindowCheck extends AbstractPageCheck {

  @Override
  public void startElement(TagNode node) {
    int line = 0;

    if (isParam(node) && hasInvalidObjectWmodeParam(node) && node.getParent() != null && FlashHelper.isFlashObject(node.getParent())) {
      line = node.getStartLinePosition();
    } else if (isEmbed(node) && hasInvalidEmbedWmodeAttribute(node) && FlashHelper.isFlashEmbed(node)) {
      line = getWmodeAttributeLine(node);
    }

    if (line != 0) {
      createViolation(line, "Set the value of the 'wmode' parameter to 'window'.");
    }
  }

  private static int getWmodeAttributeLine(TagNode node) {
    for (Attribute attribute : node.getAttributes()) {
      if ("WMODE".equalsIgnoreCase(attribute.getName())) {
        return attribute.getLine();
      }
    }

    throw new IllegalStateException();
  }

  private static boolean isParam(TagNode node) {
    return "PARAM".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean hasInvalidObjectWmodeParam(TagNode node) {
    String name = node.getAttribute("name");
    String value = node.getAttribute("value");

    return name != null &&
      value != null &&
      "WMODE".equalsIgnoreCase(name) &&
      !"WINDOW".equalsIgnoreCase(value);
  }

  private static boolean isEmbed(TagNode node) {
    return "EMBED".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean hasInvalidEmbedWmodeAttribute(TagNode node) {
    String wmode = node.getAttribute("wmode");

    return wmode != null &&
      !"WINDOW".equalsIgnoreCase(wmode);
  }

}
