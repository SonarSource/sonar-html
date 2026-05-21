/*
 * SonarQube HTML
 * Copyright (C) SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * You can redistribute and/or modify this program under the terms of
 * the Sonar Source-Available License Version 1, as published by SonarSource Sàrl.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package org.sonar.plugins.html.checks.attributes;

import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import org.sonar.check.Rule;
import org.sonar.plugins.html.api.Helpers;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Attribute;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "S8699")
public class FormMethodAttributeCheck extends AbstractPageCheck {

  private static final String METHOD_ATTRIBUTE = "method";
  private static final Set<String> VALID_METHODS = Set.of("get", "post", "dialog");
  private static final Pattern TH_ATTR_METHOD_PATTERN = Pattern.compile("(^|,)\\s*method\\s*=", Pattern.CASE_INSENSITIVE);
  private static final String MESSAGE = "Use an explicit valid \"method\" attribute on this \"<form>\" tag (\"get\", \"post\", or \"dialog\").";

  @Override
  public void startElement(TagNode node) {
    if (!node.equalsElementName("form")) {
      return;
    }

    Attribute methodAttribute = node.getProperty(METHOD_ATTRIBUTE);
    if (methodAttribute == null) {
      if (!hasThymeleafMethod(node)) {
        createViolation(node, MESSAGE);
      }
      return;
    }

    if (isDynamicMethod(methodAttribute)) {
      return;
    }

    String methodValue = methodAttribute.getValue();
    if (methodValue == null || !VALID_METHODS.contains(methodValue.trim().toLowerCase(Locale.ROOT))) {
      createViolation(node, MESSAGE);
    }
  }

  private boolean isDynamicMethod(Attribute methodAttribute) {
    return !METHOD_ATTRIBUTE.equalsIgnoreCase(methodAttribute.getName())
      || isUnifiedExpression(methodAttribute.getValue())
      || Helpers.isDynamicValue(methodAttribute.getValue(), getHtmlSourceCode());
  }

  private static boolean hasThymeleafMethod(TagNode node) {
    String thAttrValue = node.getAttribute("th:attr");
    return node.hasProperty("th:method")
      || (thAttrValue != null && TH_ATTR_METHOD_PATTERN.matcher(thAttrValue).find());
  }
}
