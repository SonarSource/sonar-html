/*
 * SonarSource :: Web :: Sonar Plugin
 * Copyright (c) 2010-2016 SonarSource SA and Matthijs Galesloot
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
package org.sonar.plugins.web.checks.scripting;

import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.node.TagNode;
import org.sonar.plugins.web.node.TextNode;

@Rule(key = "LongJavaScriptCheck")
public class LongJavaScriptCheck extends AbstractPageCheck {

  private static final int DEFAULT_MAX_LINES = 5;

  @RuleProperty(
    key = "maxLines",
    description = "Max Lines (Number)",
    defaultValue = "" + DEFAULT_MAX_LINES)
  public int maxLines = DEFAULT_MAX_LINES;

  private int linesOfCode;
  private TagNode scriptNode;

  @Override
  public void characters(TextNode textNode) {
    if (scriptNode != null) {
      linesOfCode += textNode.getLinesOfCode();

      if (linesOfCode > maxLines) {
        createViolation(scriptNode.getStartLinePosition(),
          "The length of this JS script (" + linesOfCode + ") exceeds the maximum set to " + maxLines + ".",
          Double.valueOf(linesOfCode) - Double.valueOf(maxLines));
        scriptNode = null;
      }
    }
  }

  @Override
  public void endElement(TagNode element) {
    scriptNode = null;
  }

  @Override
  public void startElement(TagNode element) {
    if ("script".equalsIgnoreCase(element.getNodeName())) {
      scriptNode = element;
      linesOfCode = 0;
    }
  }

}
