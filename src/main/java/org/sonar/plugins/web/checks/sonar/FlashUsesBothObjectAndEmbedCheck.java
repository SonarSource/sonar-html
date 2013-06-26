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
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.node.TagNode;

import java.util.List;
import java.util.Locale;

@Rule(
  key = "FlashUsesBothObjectAndEmbedCheck",
  priority = Priority.MAJOR)
public class FlashUsesBothObjectAndEmbedCheck extends AbstractPageCheck {

  private int objectLine;
  private boolean foundEmbed;

  @Override
  public void startDocument(List<Node> nodes) {
    objectLine = 0;
  }

  @Override
  public void startElement(TagNode node) {
    if (isObject(node) && isFlashObject(node)) {
      objectLine = node.getStartLinePosition();
      foundEmbed = false;
    } else if (isEmbed(node) && isFlashEmbed(node)) {
      foundEmbed = true;

      if (node.getParent() == null || !isObject(node.getParent())) {
        createViolation(node.getStartLinePosition(), "Surround this <embed> tag by an <object> one.");
      }
    }
  }

  @Override
  public void endElement(TagNode node) {
    if (isObject(node)) {
      if (objectLine != 0 && !foundEmbed) {
        createViolation(objectLine, "Add an <embed> tag within this <object> one.");
      }
      objectLine = 0;
    }
  }

  private static boolean isObject(TagNode node) {
    return "OBJECT".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isEmbed(TagNode node) {
    return "EMBED".equalsIgnoreCase(node.getNodeName());
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

}
