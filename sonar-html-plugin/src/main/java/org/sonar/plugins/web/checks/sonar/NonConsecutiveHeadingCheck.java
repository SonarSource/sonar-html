/*
 * SonarWeb :: SonarQube Plugin
 * Copyright (c) 2010-2018 SonarSource SA and Matthijs Galesloot
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
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.node.TagNode;

import java.util.List;

@Rule(key = "NonConsecutiveHeadingCheck")
public class NonConsecutiveHeadingCheck extends AbstractPageCheck {

  private final int[] firstUsage = new int[6];

  @Override
  public void startDocument(List<Node> nodes) {
    for (int i = 0; i < firstUsage.length; i++) {
      firstUsage[i] = 0;
    }
  }

  @Override
  public void startElement(TagNode node) {
    if (isHeadingTag(node)) {
      int index = node.getNodeName().charAt(1) - '1';

      if (firstUsage[index] == 0) {
        firstUsage[index] = node.getStartLinePosition();
      }
    }
  }

  @Override
  public void endDocument() {
    for (int i = firstUsage.length - 1; i > 0; i--) {
      if (firstUsage[i] != 0 && firstUsage[i - 1] == 0) {
        createViolation(firstUsage[i], "Do not skip level H" + i + ".");
      }
    }
  }

  private static boolean isHeadingTag(TagNode node) {
    return node.getNodeName().length() == 2 &&
      Character.toUpperCase(node.getNodeName().charAt(0)) == 'H' &&
      node.getNodeName().charAt(1) >= '1' &&
      node.getNodeName().charAt(1) <= '6';
  }

}
