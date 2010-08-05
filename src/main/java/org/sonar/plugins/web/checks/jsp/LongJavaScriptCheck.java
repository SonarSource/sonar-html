/*
 * Copyright (C) 2010 Matthijs Galesloot
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

package org.sonar.plugins.web.checks.jsp;

import org.sonar.check.Check;
import org.sonar.check.CheckProperty;
import org.sonar.check.IsoCategory;
import org.sonar.check.Priority;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.node.TagNode;
import org.sonar.plugins.web.node.TextNode;

/**
 * Checker to find long javascripts.
 * 
 * @author Matthijs Galesloot
 * 
 * @see http://pmd.sourceforge.net/rules/basic-jsp.html
 */
@Check(key = "LongJavaScriptCheck", title = "Long JavaScript", description = "Avoid long JavaScript", priority = Priority.CRITICAL, isoCategory = IsoCategory.Maintainability)
public class LongJavaScriptCheck extends AbstractPageCheck {

  private int linesOfCode;
  @CheckProperty(key = "maxLines", title = "Max Lines")
  private int maxLines = 5;

  private TagNode scriptNode;

  @Override
  public void characters(TextNode textNode) {
    if (scriptNode != null) {
      linesOfCode += textNode.getLinesOfCode();

      if (linesOfCode > maxLines) {
        createViolation(scriptNode);
        scriptNode = null;
      }
    }
  }

  @Override
  public void endElement(TagNode element) {
    scriptNode = null;
  }

  public int getMaxLines() {
    return maxLines;
  }

  public void setMaxLines(int maxLines) {
    this.maxLines = maxLines;
  }

  @Override
  public void startElement(TagNode element) {
    if ("script".equalsIgnoreCase(element.getNodeName())) {
      scriptNode = element;
      linesOfCode = 0;
    }
  }
}
