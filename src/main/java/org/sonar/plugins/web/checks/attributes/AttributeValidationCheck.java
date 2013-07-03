/*
 * Sonar Web Plugin
 * Copyright (C) 2010 SonarSource and Matthijs Galesloot
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
import org.sonar.check.Cardinality;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.node.Attribute;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.node.TagNode;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Checker for values of attributes. A list of values might be specified as normal values or RegExpressions.
 */
@Rule(
  key = "AttributeValidationCheck",
  priority = Priority.MAJOR,
  cardinality = Cardinality.MULTIPLE)
public class AttributeValidationCheck extends AbstractPageCheck {

  private static final String DEFAULT_ATTRIBUTES = "";
  private static final String DEFAULT_VALUES = "";

  @RuleProperty(
    key = "attributes",
    defaultValue = DEFAULT_ATTRIBUTES)
  public String attributes = DEFAULT_ATTRIBUTES;

  @RuleProperty(
    key = "values",
    defaultValue = DEFAULT_VALUES)
  public String values = DEFAULT_VALUES;

  private QualifiedAttribute[] attributesArray;
  private Pattern pattern;

  @Override
  public void startDocument(List<Node> nodes) {
    this.attributesArray = parseAttributes(attributes);
    pattern = Pattern.compile(values);
  }

  private boolean isValidValue(Attribute a) {
    if (StringUtils.isEmpty(a.getValue())) {
      return true;
    }

    Matcher m = pattern.matcher(a.getValue());
    return m.matches();
  }

  @Override
  public void startElement(TagNode element) {
    for (Attribute a : getMatchingAttributes(element, attributesArray)) {
      if (!isValidValue(a)) {
        createViolation(element.getStartLinePosition(), "The attribute '" + a.getName() + "' does not respect the value constraint: " + values);
      }
    }
  }

}
