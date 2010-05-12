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

import org.sonar.api.batch.SensorContext;
import org.sonar.check.Check;
import org.sonar.check.IsoCategory;
import org.sonar.check.Priority;
import org.sonar.plugins.web.language.WebFile;
import org.sonar.plugins.web.node.DirectiveNode;
import org.sonar.plugins.web.rules.AbstractPageCheck;

/**
 * @author Matthijs Galesloot
 * 
 * @see http://java.sun.com/developer/technicalArticles/javaserverpages/code_convention/  
 */
@Check(key = "MultiplePageDirectivesCheck", description = "Multiple page directives should be combined", priority = Priority.MAJOR, isoCategory = IsoCategory.Maintainability)
public class MultiplePageDirectivesCheck extends AbstractPageCheck {

  private int pageDirectives;
  private DirectiveNode node;
  
  @Override
  public void startDocument(SensorContext sensorContext, WebFile resource) {
    super.startDocument(sensorContext, resource);
    pageDirectives = 0;
  }

  @Override
  public void directive(DirectiveNode node) {
    if (!node.isHtml()) {
      if ("page".equalsIgnoreCase(node.getNodeName())) {
        pageDirectives++; 
        this.node = node; 
      }
    }
  }
  
  @Override
  public void endDocument() {
    if (pageDirectives > 0) {
      createViolation(node);
    }
  }
}
