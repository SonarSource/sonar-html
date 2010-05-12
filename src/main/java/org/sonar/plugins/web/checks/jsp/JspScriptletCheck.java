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

import org.apache.commons.lang.StringUtils;
import org.sonar.check.Check;
import org.sonar.check.IsoCategory;
import org.sonar.check.Priority;
import org.sonar.plugins.web.node.TagNode;
import org.sonar.plugins.web.node.TextNode;
import org.sonar.plugins.web.rules.AbstractPageCheck;

/**
 * @author Matthijs Galesloot
 * 
 *  @see http://java.sun.com/developer/technicalArticles/javaserverpages/code_convention/ 
 */
@Check(key = "JspScriptletCheck", 
    description = "Avoid scriptlets",
    priority=Priority.MAJOR, 
    isoCategory = IsoCategory.Maintainability)
public class JspScriptletCheck extends AbstractPageCheck {

  @Override
  public void startElement(TagNode element) {
    if (StringUtils.containsIgnoreCase(element.getNodeName(), "scriptlet")) {
      createViolation(element);
    }
  }
  
  @Override
  public void characters(TextNode textNode) {
    if (textNode.getCode().startsWith("<%")) {
      createViolation(textNode);
    }
  }
}
