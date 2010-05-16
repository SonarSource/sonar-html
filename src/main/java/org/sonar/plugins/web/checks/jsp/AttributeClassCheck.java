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
import org.sonar.check.IsoCategory;
import org.sonar.check.Priority;
import org.sonar.plugins.web.node.Attribute;
import org.sonar.plugins.web.node.TagNode;
import org.sonar.plugins.web.rules.AbstractPageCheck;

/**
 * Checker for occurrence of attribute class.
 * 
 * http://pmd.sourceforge.net/rules/basic-jsp.html
 * 
 * @author Matthijs Galesloot
 */
@Check(key = "AttributeClassCheck", title = "Class attribute", description = "class should not be used, use styleClass instead", priority = Priority.MAJOR, isoCategory = IsoCategory.Maintainability)
public class AttributeClassCheck extends AbstractPageCheck {

  @Override
  public void startElement(TagNode element) {

    if (element.getNodeName().indexOf(':') > 0) {
      for (Attribute a : element.getAttributes()) {

        if ("class".equalsIgnoreCase(a.getName())) {
          createViolation(element);
        }
      }
    }
  }
}