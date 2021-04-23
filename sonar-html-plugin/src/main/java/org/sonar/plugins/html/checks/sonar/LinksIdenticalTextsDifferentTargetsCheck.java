/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (c) 2010-2021 SonarSource SA and Matthijs Galesloot
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
