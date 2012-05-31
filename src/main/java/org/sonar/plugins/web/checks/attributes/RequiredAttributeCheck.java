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

package org.sonar.plugins.web.checks.attributes;

import org.apache.commons.lang.StringUtils;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.node.TagNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Checker for occurrence of required attribute, e.g. alt attribute in &lt;img&gt; tag.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
@Rule(key = "RequiredAttributeCheck", priority = Priority.MAJOR)
public class RequiredAttributeCheck extends AbstractPageCheck {

  private static final class RequiredAttribute {

    private String elementName;
    private String attributeName;

    @Override
    public String toString() {
      return elementName == null ? attributeName : elementName + "." + attributeName;
    }
  }

  @RuleProperty
  private final List<RequiredAttribute> attributes = new ArrayList<RequiredAttribute>();

  public String getAttributes() {
    return StringUtils.join(attributes, ",");
  }

  public void setAttributes(String list) {
    for (String item : trimSplitCommaSeparatedList(list)) {
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
