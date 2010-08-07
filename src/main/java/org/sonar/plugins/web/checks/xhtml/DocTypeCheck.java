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

package org.sonar.plugins.web.checks.xhtml;

import org.sonar.check.Check;
import org.sonar.check.CheckProperty;
import org.sonar.check.IsoCategory;
import org.sonar.check.Priority;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.node.DirectiveNode;
import org.sonar.plugins.web.visitor.WebSourceCode;

/**
 * Checks declaration of the DOCTYPE.
 * 
 * @author Matthijs Galesloot
 * @since 1.0
 */
@Check(key = "DocTypeCheck", title = "Document Type Compliance", description = "Document Type Compliance", priority = Priority.MINOR, isoCategory = IsoCategory.Maintainability)
public class DocTypeCheck extends AbstractPageCheck {

  private boolean hasDocType;

  @CheckProperty(key = "dtd", description = "Document Type")
  private String dtd;

  public String getDtd() {
    return dtd;
  }

  public void setDtd(String dtd) {
    this.dtd = dtd;
  }

  @Override
  public void startDocument(WebSourceCode webSourceCode) {
    super.startDocument(webSourceCode);
    hasDocType = false;
  }

  @Override
  public void directive(DirectiveNode node) {
    if ("DOCTYPE".equals(node.getNodeName())) {
      hasDocType = true;

      if (dtd!= null) {
        boolean containsDtd = false;

        for (int i = 0; i < node.getAttributes().size(); i++) {
          if (node.getAttributes().get(i).getName().equals(dtd)) {
            containsDtd = true;
            break;
          }
        }
        if (!containsDtd) {
          createViolation(0);
        }
      }
    }
  }

  @Override
  public void endDocument() {
    if (!hasDocType) {
      createViolation(0);
    }
  }
}