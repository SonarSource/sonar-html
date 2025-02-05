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
package org.sonar.plugins.html.checks.sonar;

import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;

import java.util.List;

@Rule(key = "FlashUsesBothObjectAndEmbedCheck")
public class FlashUsesBothObjectAndEmbedCheck extends AbstractPageCheck {

  private TagNode object;
  private boolean foundEmbed;

  @Override
  public void startDocument(List<Node> nodes) {
    object = null;
  }

  @Override
  public void startElement(TagNode node) {
    if (isObject(node) && FlashHelper.isFlashObject(node)) {
      object = node;
      foundEmbed = false;
    } else if (isEmbed(node) && FlashHelper.isFlashEmbed(node)) {
      foundEmbed = true;

      if (node.getParent() == null || !isObject(node.getParent())) {
        createViolation(node, "Surround this <embed> tag by an <object> one.");
      }
    }
  }

  @Override
  public void endElement(TagNode node) {
    if (isObject(node)) {
      if (object != null && !foundEmbed) {
        createViolation(object, "Add an <embed> tag within this <object> one.");
      }
      object = null;
    }
  }

  private static boolean isObject(TagNode node) {
    return "OBJECT".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isEmbed(TagNode node) {
    return "EMBED".equalsIgnoreCase(node.getNodeName());
  }

}
