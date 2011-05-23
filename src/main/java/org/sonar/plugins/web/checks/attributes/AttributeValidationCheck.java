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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.node.Attribute;
import org.sonar.plugins.web.node.TagNode;

/**
 * Checker for values of attributes. A list of values might be specified as normal values or RegExpressions.
 *
 * @author Matthijs Galesloot
 * @since 1.1
 */
@Rule(key = "AttributeValidationCheck", name = "Attribute Validation", description = "Invalid value of attribute",
    priority = Priority.MAJOR)
public class AttributeValidationCheck extends AbstractPageCheck {

  @RuleProperty(key = "attributes", description = "List of attributes, comma separated.")
  private QualifiedAttribute[] attributes;

  @RuleProperty(key = "values", description = "List of values, comma separated. Regular expressions are supported.")
  private String[] values;

  private final List<Pattern> patterns = new ArrayList<Pattern>();

  public String getAttributes() {
    return getAttributesAsString(attributes);
  }

  public String getValues() {
    if (values != null) {
      return StringUtils.join(values, ",");
    }
    return "";
  }

  private boolean isValidValue(Attribute a) {

    if (StringUtils.isEmpty(a.getValue())) {
      return true;
    }

    for (Pattern pattern : patterns) {
      Matcher m = pattern.matcher(a.getValue());
      if (m.matches()) {
        return true;
      }
    }

    return false; // no match was found
  }

  public void setAttributes(String qualifiedAttributes) {
    this.attributes = parseAttributes(qualifiedAttributes);
  }

  public void setValues(String list) {
    values = StringUtils.split(list, ",");
    values = StringUtils.stripAll(values);

    patterns.clear();
    for (String parameter : values) {
      Pattern pattern = Pattern.compile(parameter);
      patterns.add(pattern);
    }
  }

  @Override
  public void startElement(TagNode element) {

    if (attributes == null) {
      return;
    }

    for (Attribute a : getMatchingAttributes(element, attributes)) {
      if ( !isValidValue(a)) {
        createViolation(element.getStartLinePosition(), getRule().getDescription() + ": " + a.getName());
      }
    }
  }
}
