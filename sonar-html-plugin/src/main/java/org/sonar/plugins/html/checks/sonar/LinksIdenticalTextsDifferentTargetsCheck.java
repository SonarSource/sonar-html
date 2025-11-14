/*
 * SonarQube HTML
 * Copyright (C) 2010-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource Sàrl.
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

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;
import org.sonar.plugins.html.node.TextNode;

@Rule(key = "LinksIdenticalTextsDifferentTargetsCheck")
public class LinksIdenticalTextsDifferentTargetsCheck extends AbstractPageCheck {

  private boolean inLink;
  private final Map<String, Link> links = new HashMap<>();

  private final StringBuilder text = new StringBuilder();
  private String target = "";
  private int line;

  @Override
  public void startDocument(List<Node> nodes) {
    links.clear();
    inLink = false;
  }

  @Override
  public void startElement(TagNode node) {
    if (isA(node)) {
      inLink = true;

      text.delete(0, text.length());
      target = getTarget(node);
      line = node.getStartLinePosition();
    }
  }

  private static String getTarget(TagNode node) {
    String target = node.getPropertyValue("href");
    return target == null ? "" : target;
  }

  @Override
  public void characters(TextNode textNode) {
    if (inLink) {
      text.append(textNode.getCode());
    }
  }

  @Override
  public void endElement(TagNode node) {
    if (isA(node)) {
      inLink = false;

      String upperText = text.toString().toUpperCase(Locale.ENGLISH).trim();
      if (!upperText.isEmpty()) {
        if (links.containsKey(upperText)) {
          Link previousLink = links.get(upperText);

          if (!target.equals(previousLink.getTarget())) {
            createViolation(line, "Use distinct texts or point to the same target for this link and the one at line " + previousLink.getLine() + ".");
          }
        } else {
          links.put(upperText, new Link(line, target));
        }
      }
    }
  }

  private static boolean isA(TagNode node) {
    return "A".equalsIgnoreCase(node.getNodeName());
  }

  private static class Link {

    private final int line;
    private final String target;

    public Link(int line, String target) {
      this.line = line;
      this.target = target;
    }

    public int getLine() {
      return line;
    }

    public String getTarget() {
      return target;
    }

  }

}
