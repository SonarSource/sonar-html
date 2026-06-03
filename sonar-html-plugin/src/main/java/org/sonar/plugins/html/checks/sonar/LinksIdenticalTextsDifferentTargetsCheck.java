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
package org.sonar.plugins.html.checks.sonar;

import java.util.HashMap;
import java.util.IdentityHashMap;
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
  // Outer key: parent TagNode (null = document root). Inner key: uppercased link text.
  private final Map<TagNode, Map<String, Link>> linksByParent = new IdentityHashMap<>();

  private final StringBuilder text = new StringBuilder();
  private String target = "";
  private int line;
  private TagNode linkParent;

  @Override
  public void startDocument(List<Node> nodes) {
    linksByParent.clear();
    inLink = false;
  }

  @Override
  public void startElement(TagNode node) {
    if (isA(node)) {
      inLink = true;
      text.delete(0, text.length());
      target = getTarget(node);
      line = node.getStartLinePosition();
      linkParent = node.getParent();
    }
  }

  private static String getTarget(TagNode node) {
    String t = node.getPropertyValue("href");
    return t == null ? "" : t;
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
        Map<String, Link> siblingLinks = linksByParent.computeIfAbsent(linkParent, k -> new HashMap<>());
        if (siblingLinks.containsKey(upperText)) {
          Link previousLink = siblingLinks.get(upperText);
          if (!target.equals(previousLink.getTarget())) {
            createViolation(line, "Use distinct texts or point to the same target for this link and the one at line " + previousLink.getLine() + ".");
            siblingLinks.put(upperText, new Link(line, target));
          }
        } else {
          siblingLinks.put(upperText, new Link(line, target));
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
