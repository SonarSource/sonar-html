/*
 * Sonar Web Plugin
 * Copyright (C) 2010 Matthijs Galesloot
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.plugins.web.checks.xhtml;

import org.sonar.check.IsoCategory;
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
@Rule(key = "DocTypeCheck", name = "Document Type Compliance", description = "Document Type Compliance", priority = Priority.MINOR,
    isoCategory = IsoCategory.Maintainability)
public class DocTypeCheck extends AbstractPageCheck {

  private boolean hasDocType;

  @RuleProperty(key = "dtd", description = "Document Type")
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
        if ( !containsDtd) {
          createViolation(0);
        }
      }
    }
  }

  @Override
  public void endDocument() {
    if ( !hasDocType) {
      createViolation(0);
    }
  }
}