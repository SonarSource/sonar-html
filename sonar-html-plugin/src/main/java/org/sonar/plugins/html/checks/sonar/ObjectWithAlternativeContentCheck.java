/*
 * SonarHTML :: SonarQube Plugin
 * Copyright (c) 2010-2019 SonarSource SA and Matthijs Galesloot
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

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Node;
import org.sonar.plugins.html.node.TagNode;
import org.sonar.plugins.html.node.TextNode;

@Rule(key = "S5264")
public class ObjectWithAlternativeContentCheck extends AbstractPageCheck {

  private static class ObjectEntry {

    TagNode object;
    boolean hasAlternativeContent;
    
    public ObjectEntry(TagNode object) {
      this.object = object;
      this.hasAlternativeContent = false;
    }
  }

  private Deque<ObjectEntry> stack = new LinkedList<>();

  @Override
  public void startDocument(List<Node> nodes) {
    stack.clear();
  }

  @Override
  public void endDocument() {
    stack.clear();
  }

  @Override
  public void startElement(TagNode node) {
    if (!stack.isEmpty()) {
      stack.peekFirst().hasAlternativeContent = true;
    }
    if (isObject(node)) {
      stack.addFirst(new ObjectEntry(node));
    }
  }

  @Override
  public void endElement(TagNode node) {
    if (isObject(node) && !stack.isEmpty()) {
      ObjectEntry entry = stack.removeFirst();
      if (!entry.hasAlternativeContent) {
        createViolation(entry.object.getStartLinePosition(), "Add an accessible content to this \"<object>\" tag.");
      }
    }
  }

  @Override
  public void characters(TextNode textNode) {
    if (!textNode.isBlank() && !stack.isEmpty()) {
      stack.peekFirst().hasAlternativeContent = true;
    }
  }
  
  private static boolean isObject(TagNode node) {
    return "OBJECT".equalsIgnoreCase(node.getNodeName());
  }
}
