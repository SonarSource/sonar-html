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

package org.sonar.plugins.web.checks.jsp;

import org.sonar.check.IsoCategory;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.node.DirectiveNode;
import org.sonar.plugins.web.visitor.WebSourceCode;

/**
 * Checker to find multiple page directives, where 1 page directive would be preferred.
 *
 * @see http://java.sun.com/developer/technicalArticles/javaserverpages/code_convention/ paragraph JSP Page Directive(s)
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
@Rule(key = "MultiplePageDirectivesCheck", name ="Multiple Page Directive", description = "Avoid multiple page directives",
    priority = Priority.MINOR, isoCategory = IsoCategory.Maintainability)
public class MultiplePageDirectivesCheck extends AbstractPageCheck {

  private static boolean isImportDirective(DirectiveNode node) {
    return node.getAttributes().size() == 1 && node.getAttribute("import") != null;
  }

  private DirectiveNode node;

  private int pageDirectives;

  @Override
  public void directive(DirectiveNode node) {
    if ( !node.isHtml() && "page".equalsIgnoreCase(node.getNodeName()) && !isImportDirective(node)) {
      pageDirectives++;
      this.node = node;
    }
  }

  @Override
  public void endDocument() {
    if (pageDirectives > 1) {
      createViolation(node);
    }
  }

  @Override
  public void startDocument(WebSourceCode webSourceCode) {
    super.startDocument(webSourceCode);
    pageDirectives = 0;
  }
}
