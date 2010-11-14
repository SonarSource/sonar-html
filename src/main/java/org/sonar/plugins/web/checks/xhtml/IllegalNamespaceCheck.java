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

import org.apache.commons.lang.StringUtils;
import org.sonar.check.IsoCategory;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.node.Attribute;
import org.sonar.plugins.web.node.TagNode;

/**
 * Checker for occurrence of disallowed namespaces.
 *
 * Checks the namespaces in the root element of the XML document. A list of illegal namespaces can be configured as a property of the check.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
@Rule(key = "IllegalNamespaceCheck", name ="Illegal Namespace", description = "namespace should not be used", priority = Priority.MAJOR,
    isoCategory = IsoCategory.Reliability)
public class IllegalNamespaceCheck extends AbstractPageCheck {

  @RuleProperty(key = "namespaces", description = "Namespaces")
  private String[] namespaces;
  private boolean visited;

  public String getNamespaces() {
    if (namespaces != null) {
      return StringUtils.join(namespaces, ",");
    }
    return "";
  }

  public void setNamespaces(String list) {
    namespaces = StringUtils.split(list, ",");
  }

  @Override
  public void startElement(TagNode element) {

    if (visited || namespaces == null) {
      return;
    }

    for (Attribute a : element.getAttributes()) {

      if (StringUtils.startsWithIgnoreCase(a.getName(), "xmlns")) {
        for (String namespace : namespaces) {
          if (a.getValue().equalsIgnoreCase(namespace)) {
            createViolation(element);
          }
        }
      }
    }
  }
}
