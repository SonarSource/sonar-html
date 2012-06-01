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

package org.sonar.plugins.web.checks.header;

import org.sonar.check.Cardinality;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.node.DirectiveNode;
import org.sonar.plugins.web.visitor.WebSourceCode;

/**
 * Checks declaration of the DOCTYPE.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
@Rule(key = "DocTypeCheck", priority = Priority.MINOR, cardinality = Cardinality.MULTIPLE)
public class DocTypeCheck extends AbstractPageCheck {

  private boolean hasDocType;

  @RuleProperty
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

      if (dtd != null) {
        boolean containsDtd = false;

        for (int i = 0; i < node.getAttributes().size(); i++) {
          if (node.getAttributes().get(i).getName().equals(dtd)) {
            containsDtd = true;
            break;
          }
        }
        if (!containsDtd) {
          createViolation(0, "The DOCTYPE does not contain any DTD.");
        }
      }
    }
  }

  @Override
  public void endDocument() {
    if (!hasDocType) {
      createViolation(0, "DOCTYPE is missing on this file.");
    }
  }
}
