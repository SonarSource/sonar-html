/*
 * Sonar Web Plugin
 * Copyright (C) 2010 Matthijs Galesloot
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

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.node.Attribute;
import org.sonar.plugins.web.node.TagNode;

import java.util.Locale;

@Rule(
  key = "WmodeIsWindowCheck",
  priority = Priority.MAJOR)
public class WmodeIsWindowCheck extends AbstractPageCheck {

  @Override
  public void startElement(TagNode node) {
    int line = 0;

    if (isParam(node) && hasInvalidObjectWmodeParam(node) && node.getParent() != null && isFlashObject(node.getParent())) {
      line = node.getStartLinePosition();
    } else if (isEmbed(node) && hasInvalidEmbedWmodeAttribute(node) && isFlashEmbed(node)) {
      line = getWmodeAttributeLine(node);
    }

    if (line != 0) {
      createViolation(line, "Set the value of the 'wmode' parameter to 'window'.");
    }
  }

  private static boolean isFlashObject(TagNode node) {
    return hasFlashClassId(node.getAttribute("classid")) ||
      hasFlashType(node.getAttribute("type")) ||
      hasFlashExtension(node.getAttribute("data"));
  }

  private static boolean hasFlashClassId(String classId) {
    return classId != null && classId.equalsIgnoreCase("CLSID:D27CDB6E-AE6D-11CF-96B8-444553540000");
  }

  private static boolean hasFlashType(String type) {
    return type != null && type.toUpperCase(Locale.ENGLISH).contains("X-SHOCKWAVE-FLASH");
  }

  private static boolean hasFlashExtension(String file) {
    return file != null && file.toUpperCase(Locale.ENGLISH).endsWith(".SWF");
  }

  private static boolean isFlashEmbed(TagNode node) {
    return hasFlashType(node.getAttribute("type")) ||
      hasFlashExtension(node.getAttribute("src"));
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
