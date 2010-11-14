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
 * Checker for occurrence of disallowed attributes.
 *
 * e.g. class attribute should not be used, but styleClass instead.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
@Rule(key = "IllegalAttributeCheck", name ="Illegal Attribute", description = "attribute should not be used", priority = Priority.MAJOR,
    isoCategory = IsoCategory.Reliability)
public class IllegalAttributeCheck extends AbstractPageCheck {

  @RuleProperty(key = "attributes", description = "Attributes")
  private QualifiedAttribute[] attributes;

  public String getAttributes() {
    return getAttributesAsString(attributes);
  }

  public void setAttributes(String qualifiedAttributes) {
    this.attributes = parseAttributes(qualifiedAttributes);
  }

  @Override
  public void startElement(TagNode element) {

    if (attributes == null) {
      return;
    }

    for (QualifiedAttribute qualifiedAttribute : attributes) {
      if (qualifiedAttribute.getNodeName() == null
          || StringUtils.equalsIgnoreCase(element.getLocalName(), qualifiedAttribute.getNodeName())
          || StringUtils.equalsIgnoreCase(element.getNodeName(), qualifiedAttribute.getNodeName())) {
        for (Attribute a : element.getAttributes()) {

          if (qualifiedAttribute.getAttributeName().equalsIgnoreCase(a.getName())) {
            createViolation(element.getStartLinePosition(), getRule().getDescription() + ": " + a.getName());
          }
        }
      }
    }
  }
}
