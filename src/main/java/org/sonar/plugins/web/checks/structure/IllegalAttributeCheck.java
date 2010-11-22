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

package org.sonar.plugins.web.checks.structure;

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
@Rule(key = "IllegalAttributeCheck", name = "Illegal Attribute", description = "attribute should not be used", priority = Priority.MAJOR,
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
