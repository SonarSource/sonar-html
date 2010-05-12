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
import org.sonar.plugins.web.language.WebFile;
import org.sonar.plugins.web.node.CommentNode;
import org.sonar.plugins.web.node.TagNode;
import org.sonar.plugins.web.rules.AbstractPageCheck;

/**
 * Header checker for JSP files. 
 * 
 * @see http://java.sun.com/developer/technicalArticles/javaserverpages/code_convention/ 
 * 
 * @author Matthijs Galesloot
 */
@Check(key = "HeaderCheck", 
    description = "Header has server side comment", 
    isoCategory = IsoCategory.Maintainability)
public class HeaderCheck extends AbstractPageCheck {

  private boolean hasHeader; 
  private boolean raised; 
  
  @Override
  public void startDocument(SensorContext sensorContext, WebFile resource) {
    super.startDocument(sensorContext, resource);
    hasHeader = false;
    raised = false; 
  }
  
  @Override
  public void comment(CommentNode node) {
    super.comment(node);
    hasHeader = true; 
  }
  
  @Override
  public void startElement(TagNode node) {
    if (!hasHeader && !raised) {
      createViolation(node);
      raised = true; 
    }
  }

}