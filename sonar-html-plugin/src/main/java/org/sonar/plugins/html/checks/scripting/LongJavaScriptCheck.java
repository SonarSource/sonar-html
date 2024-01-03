/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (c) 2010-2024 SonarSource SA and Matthijs Galesloot
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
package org.sonar.plugins.html.checks.scripting;

import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;
import org.sonar.plugins.html.node.TextNode;

@Rule(key = "LongJavaScriptCheck")
public class LongJavaScriptCheck extends AbstractPageCheck {

  private static final int DEFAULT_MAX_LINES = 5;

  private final StringBuilder text = new StringBuilder();

  private TagNode scriptNode;


  @RuleProperty(
    key = "maxLines",
    description = "Max Lines (Number)",
    defaultValue = "" + DEFAULT_MAX_LINES)
  public int maxLines = DEFAULT_MAX_LINES;

  @Override
  public void startElement(TagNode node) {
    if ("script".equalsIgnoreCase(node.getNodeName())) {
      scriptNode = node;
      text.delete(0, text.length());
    }
  }

  @Override
  public void characters(TextNode textNode) {
    if (scriptNode != null) {
      text.append(textNode.getCode());
    }
  }

  @Override
  public void endElement(TagNode element) {
    if ("script".equalsIgnoreCase(element.getNodeName()) && scriptNode != null) {
      int linesOfCode = (int) text.toString().trim().lines().count();
      if (linesOfCode > maxLines) {
        createViolation(scriptNode.getStartLinePosition(),
          "The length of this JS script (" + linesOfCode + ") exceeds the maximum set to " + maxLines + ".",
          (double) linesOfCode - (double) maxLines);
      }
      scriptNode = null;
    }
  }

}
