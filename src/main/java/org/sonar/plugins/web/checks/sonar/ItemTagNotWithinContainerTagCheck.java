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
import org.sonar.plugins.web.node.TagNode;

@Rule(
  key = "ItemTagNotWithinContainerTagCheck",
  priority = Priority.MAJOR)
public class ItemTagNotWithinContainerTagCheck extends AbstractPageCheck {

  @Override
  public void startElement(TagNode node) {
    if (isLi(node) && (node.getParent() == null || !isLi(node.getParent()) && !isUlOrOl(node.getParent()))) {
      createViolation(node.getStartLinePosition(), "Surround this <" + node.getNodeName() + "> item tag by a <ul> or <ol> container one.");
    } else if (isDt(node) && (node.getParent() == null || !isDl(node.getParent()))) {
      createViolation(node.getStartLinePosition(), "Surround this <" + node.getNodeName() + "> item tag by a <dl> container one.");
    }
  }

  private static boolean isLi(TagNode node) {
    return "LI".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isDt(TagNode node) {
    return "DT".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isUlOrOl(TagNode node) {
    return isUl(node) || isOl(node);
  }

  private static boolean isUl(TagNode node) {
    return "UL".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isOl(TagNode node) {
    return "OL".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isDl(TagNode node) {
    return "DL".equalsIgnoreCase(node.getNodeName());
  }

}
