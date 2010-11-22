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

package org.sonar.plugins.web.checks.structure;

import org.apache.commons.lang.StringUtils;
import org.sonar.check.IsoCategory;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.checks.Utils;
import org.sonar.plugins.web.node.TagNode;

/**
 * Checker for occurrence of disallowed elements.
 * 
 * e.g. element <applet> should not be used.
 * 
 * @author Matthijs Galesloot
 * @since 1.0
 */
@Rule(key = "IllegalElementCheck", name = "IllegalElement", description = "element should not be used", priority = Priority.MAJOR,
    isoCategory = IsoCategory.Reliability)
public class IllegalElementCheck extends AbstractPageCheck {

  @RuleProperty(key = "elements", description = "elements")
  private String[] elements;

  public String getElements() {
    if (elements != null) {
      return StringUtils.join(elements, ",");
    }
    return "";
  }

  public void setElements(String elementList) {
    elements = Utils.trimSplitCommaSeparatedList(elementList);
  }

  @Override
  public void startElement(TagNode element) {

    if (elements == null) {
      return;
    }

    for (String elementName : elements) {
      if (StringUtils.equalsIgnoreCase(element.getLocalName(), elementName)
          || StringUtils.equalsIgnoreCase(element.getNodeName(), elementName)) {
        createViolation(element.getStartLinePosition(), getRule().getDescription() + ": " + elementName);
      }
    }
  }
}
