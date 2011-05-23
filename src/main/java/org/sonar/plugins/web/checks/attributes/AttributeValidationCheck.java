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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
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
 * Checker for values of attributes.
 *
 * @author Matthijs Galesloot
 * @since 1.1
 */
@Rule(key = "AttributeValidationCheck", name = "Attribute Validation", description = "values of attribute is invalid",
    priority = Priority.MAJOR)
public class AttributeValidationCheck extends AbstractPageCheck {

  private enum DataType {
    code, email, url
  }

  /**
   * Check that a given string is a well-formed email address
   *
   * Implement this http://www.ex-parrot.com/~pdw/Mail-RFC822-Address.html regex in java
   *
   * @see copied from EmailValidator in Hibernate Validation library.
   */
  private static class EmailValidator {
    private static String ATOM = "[^\\x00-\\x1F^\\(^\\)^\\<^\\>^\\@^\\,^\\;^\\:^\\\\^\\\"^\\.^\\[^\\]^\\s]";
    private static String DOMAIN = "(" + ATOM + "+(\\." + ATOM + "+)*";
    private static String IP_DOMAIN = "\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\]";

    private static final java.util.regex.Pattern pattern;

    static {
      pattern = java.util.regex.Pattern.compile(
          "^" + ATOM + "+(\\." + ATOM + "+)*@"
               + DOMAIN
               + "|"
               + IP_DOMAIN
               + ")$",
          java.util.regex.Pattern.CASE_INSENSITIVE
      );
    }

    public static boolean isValid(Object value) {
      if ( value == null ) {
        return true;
      }
      if ( !( value instanceof String ) ) {
        return false;
      }
      String string = (String) value;
      if ( string.length() == 0 ) {
        return true;
      }
      Matcher m = pattern.matcher( string );
      return m.matches();
    }
  }

  @RuleProperty(key = "attributes", description = "Attributes")
  private QualifiedAttribute[] attributes;

  @RuleProperty(key = "parameters", description = "Parameters")
  private String[] parameters;

  @RuleProperty(key = "type", description = "Type")
  private DataType type;

  private ArrayList<Pattern> patterns;

  public String getAttributes() {
    return getAttributesAsString(attributes);
  }

  public String getParameters() {
    if (parameters != null) {
      return StringUtils.join(parameters, ",");
    }
    return "";
  }

  public String getType() {
    return type.toString();
  }

  private boolean isValidValue(Attribute a) {

    String value = a.getValue();
    if ( !StringUtils.isEmpty(value)) {
      switch (type) {
        case code:
          return validateCode(value);
        case email:
          return EmailValidator.isValid(value);
        case url:
          try {
            new URL(value);
            return true;
          } catch (MalformedURLException e) {
            return false;
          }
        default:
          return true;
      }
    }
    return true;
  }

  private boolean validateCode(String value) {
    compilePatterns();
    for (Pattern pattern : patterns) {
      Matcher m = pattern.matcher(value);
      if (m.matches()) {
        return true;
      }
    }
    return false; // no match was found
  }

  private void compilePatterns() {
    if (patterns == null) {
      patterns = new ArrayList<Pattern>();
      if (parameters != null) {
        for (String parameter : parameters) {
          Pattern pattern = Pattern.compile(parameter);
          patterns.add(pattern);
        }
      }
    }
  }

  public void setAttributes(String qualifiedAttributes) {
    this.attributes = parseAttributes(qualifiedAttributes);
  }

  public void setParameters(String list) {
    parameters = StringUtils.split(list, ",");
    parameters = StringUtils.stripAll(parameters);
  }

  public void setType(DataType type) {
    this.type = type;
  }

  public void setType(String type) {
    this.type = DataType.valueOf(type);
  }

  @Override
  public void startElement(TagNode element) {

    if (attributes == null || type == null) {
      return;
    }

    for (QualifiedAttribute qualifiedAttribute : attributes) {
      if (qualifiedAttribute.getNodeName() == null
          || StringUtils.equalsIgnoreCase(element.getLocalName(), qualifiedAttribute.getNodeName())
          || StringUtils.equalsIgnoreCase(element.getNodeName(), qualifiedAttribute.getNodeName())) {
        for (Attribute a : element.getAttributes()) {
          if (qualifiedAttribute.getAttributeName().equalsIgnoreCase(a.getName()) && !isValidValue(a)) {
            createViolation(element.getStartLinePosition(), getRule().getDescription() + ": " + a.getName());
          }
        }
      }
    }
  }
}
