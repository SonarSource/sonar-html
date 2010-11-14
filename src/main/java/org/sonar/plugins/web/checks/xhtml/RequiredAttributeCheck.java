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


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.sonar.check.IsoCategory;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.checks.Utils;
import org.sonar.plugins.web.node.TagNode;

/**
 * Checker for occurrence of required attribute, e.g. alt attribute in &lt;img&gt; tag.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
@Rule(key = "RequiredAttributeCheck", name ="RequiredAttribute", description = "Required attribute must be used",
    priority = Priority.MAJOR, isoCategory = IsoCategory.Portability)
public class RequiredAttributeCheck extends AbstractPageCheck {

  private static final class RequiredAttribute {

    private String elementName;
    private String attributeName;

    @Override
    public String toString() {
      return elementName == null ? attributeName : elementName + "." + attributeName;
    }
  }

  @RuleProperty(key = "attributes", description = "attributes")
  private final List<RequiredAttribute> attributes = new ArrayList<RequiredAttribute>();

  public String getAttributes() {
     return StringUtils.join(attributes, ",");
  }

  public void setAttributes(String list) {
    for (String item : Utils.trimSplitCommaSeparatedList(list)) {
      String[] pair = StringUtils.split(item, ".");
      if (pair.length > 1) {
        RequiredAttribute a = new RequiredAttribute();
        a.elementName = pair[0];
        a.attributeName = pair[1];
        attributes.add(a);
      }
    }
  }

  @Override
  public void startElement(TagNode node) {
    for (RequiredAttribute attribute : attributes) {
      if (node.equalsElementName(attribute.elementName) && node.getAttribute(attribute.attributeName) == null) {
        createViolation(node);
      }
    }
  }
}
