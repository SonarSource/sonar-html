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
import org.sonar.plugins.web.node.TextNode;
import org.sonar.plugins.web.rules.AbstractPageCheck;

/**
 * Check for required white space.
 * 
 * @see http://java.sun.com/developer/technicalArticles/javaserverpages/code_convention/
 * 
 * @author Matthijs Galesloot
 */
@Check(key = "WhiteSpaceAroundCheck", description = "White space around", isoCategory = IsoCategory.Maintainability)
public class WhiteSpaceAroundCheck extends AbstractPageCheck {

  @Override
  public void characters(TextNode textNode) {
   
    String code = textNode.getCode();

    if (code.startsWith("<%=") && code.length() > 2 && !Character.isWhitespace(code.charAt(2))) {
      createViolation(textNode);
    }

//    if (code.endsWith("%>") && code.length() > 2) {
//      char c = !Character.isWhitespace(code.charAt(code.length() - 2))) {
//    }
//      createViolation(textNode);
//    }
  }
}