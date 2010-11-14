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
import org.sonar.plugins.web.node.CommentNode;
import org.sonar.plugins.web.node.DirectiveNode;
import org.sonar.plugins.web.visitor.WebSourceCode;

/**
 * Checker for occurrence of html comments.
 *
 * HTML comment is not allowed in JSP files, use server side comment instead. The check allows HTML comment in XHTML files, recognized by
 * its xml declaration.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
@Rule(key = "AvoidHtmlCommentCheck", name ="Avoid Html Comment", description = "Avoid Html Comment", priority = Priority.MINOR,
    isoCategory = IsoCategory.Efficiency)
public class AvoidHtmlCommentCheck extends AbstractPageCheck {

  private boolean xmlDocument;

  @Override
  public void comment(CommentNode node) {
    if ( !xmlDocument && node.isHtml()) {
      createViolation(node);
    }
  }

  @Override
  public void directive(DirectiveNode node) {
    if (node.isXml()) {
      xmlDocument = true;
    }
  }

  @Override
  public void startDocument(WebSourceCode webSourceCode) {
    super.startDocument(webSourceCode);
    xmlDocument = false;
  }
}