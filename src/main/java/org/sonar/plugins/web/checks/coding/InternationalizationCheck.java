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

package org.sonar.plugins.web.checks.coding;

import org.apache.commons.lang.StringUtils;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.node.TagNode;
import org.sonar.plugins.web.node.TextNode;

/**
 * Checker to find hardcoded labels and messages.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
@Rule(key = "InternationalizationCheck", name = "Labels Internationalization",
    description = "Labels should be defined in the resource bundle", priority = Priority.MINOR)
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
    if ( !isUnifiedExpression(textNode.getCode()) && !isPunctuationOrSpace(textNode.getCode())) {
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
            if (value.length() > 0 && !isUnifiedExpression(value) && !isPunctuationOrSpace(value)) {
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