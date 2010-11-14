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

import org.apache.commons.lang.StringUtils;
import org.sonar.check.IsoCategory;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.checks.Utils;
import org.sonar.plugins.web.node.TagNode;
import org.sonar.plugins.web.node.TextNode;

/**
 * Checker to find hardcoded labels and messages.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
@Rule(key = "InternationalizationCheck", name = "Labels Internationalization",
    description = "Labels should be defined in the resource bundle", priority = Priority.MINOR, isoCategory = IsoCategory.Maintainability)
public class InternationalizationCheck extends AbstractPageCheck {

  private static final String PUNCTUATIONS_AND_SPACE = " \t\n\r|-%:,.?!/,'\"";

  @RuleProperty(key = "attributes", defaultValue = "outputLabel.value, outputText.value", description = "Attributes")
  private QualifiedAttribute[] attributes;

  public String getAttributes() {
    return getAttributesAsString(attributes);
  }

  public void setAttributes(String qualifiedAttributes) {
    this.attributes = parseAttributes(qualifiedAttributes);
  }

  @Override
  public void characters(TextNode textNode) {
    if ( !Utils.isUnifiedExpression(textNode.getCode()) && !isPunctuationOrSpace(textNode.getCode())) {
      createViolation(textNode);
    }
  }

  @Override
  public void startElement(TagNode element) {
    if (attributes != null) {
      for (QualifiedAttribute attribute : attributes) {
        if (element.equalsElementName(attribute.getNodeName())) {
          String value = element.getAttribute(attribute.getAttributeName());
          if (value != null) {
            value = value.trim();
            if (value.length() > 0 && !Utils.isUnifiedExpression(value) && !isPunctuationOrSpace(value)) {
              createViolation(element);
              return;
            }
          }
        }
      }
    }
  }

  private static boolean isPunctuationOrSpace(String value) {
    return StringUtils.containsAny(value, PUNCTUATIONS_AND_SPACE);
  }
}