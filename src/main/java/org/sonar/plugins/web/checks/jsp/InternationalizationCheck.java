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

package org.sonar.plugins.web.checks.jsp;

import org.apache.commons.lang.StringUtils;
import org.sonar.check.Check;
import org.sonar.check.CheckProperty;
import org.sonar.check.IsoCategory;
import org.sonar.check.Priority;
import org.sonar.plugins.web.node.TagNode;
import org.sonar.plugins.web.rules.AbstractPageCheck;

/**
 * Checker to find hardcoded labels and messages.
 * 
 * @author Matthijs Galesloot
 */
@Check(key = "InternationalizationCheck", title = "Labels Internationalization", description = "Labels should be defined in the resource bundle", priority = Priority.MINOR, isoCategory = IsoCategory.Maintainability)
public class InternationalizationCheck extends AbstractPageCheck {

  private static class QualifiedAttribute {

    String attributeName;

    String nodeName;
    private QualifiedAttribute(String nodeName, String attributeName) {
      this.nodeName = nodeName;
      this.attributeName = attributeName;
    }
  }

  @CheckProperty(key = "attributes", description = "Attributes")
  private QualifiedAttribute[] attributes = new QualifiedAttribute[] { new QualifiedAttribute("outputLabel", "value") };

  public String getAttributes() {
    StringBuilder sb = new StringBuilder();
    for (QualifiedAttribute a : attributes) {
      if (sb.length() > 0) {
        sb.append(",");
      }
      sb.append(a.nodeName);
      sb.append('.');
      sb.append(a.attributeName);
    }
    return sb.toString();
  }

  public void setAttributes(String attributesList) {
    String[] qualifiedAttributeList = StringUtils.split(attributesList, ",");

    attributes = new QualifiedAttribute[qualifiedAttributeList.length];
    int n = 0;
    for (String qualifiedAttribute : qualifiedAttributeList) {
      qualifiedAttribute = qualifiedAttribute.trim();
      attributes[n++] = new QualifiedAttribute(StringUtils.substringBefore(qualifiedAttribute, "."), StringUtils.substringAfter(
          qualifiedAttribute, "."));
    }
  }

  @Override
  public void startElement(TagNode element) {
    for (QualifiedAttribute attribute : attributes) {
      if (attribute.nodeName.equals(element.getLocalName())) {
        String value = element.getAttribute(attribute.attributeName);
        if (value != null && value.startsWith("#{")) {
          createViolation(element);
          return;
        }
      }
    }
  }
}