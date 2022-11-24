/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (c) 2010-2022 SonarSource SA and Matthijs Galesloot
 * sonarqube@googlegroups.com
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
package org.sonar.plugins.html.checks.sonar;

import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.Attribute;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "S5254")
public class LangAttributeCheck extends AbstractPageCheck {

  @Override
  public void startElement(TagNode node) {
    if (isHtmlTag(node) && !hasLangAttribute(node)) {
      createViolation(node, "Add \"lang\" and/or \"xml:lang\" attributes to this \"<html>\" element");
    }
  }

  private static boolean isHtmlTag(TagNode node) {
    return "HTML".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean hasLangAttribute(TagNode node) {
    return node.hasProperty("lang")
      || node.hasProperty("xml:lang")
      || hasWordPressLangAttribute(node)
      || hasThymeleafLangAttribute(node);
  }

  /**
   * Using WordPress, HTML attributes can be set using the php function `language_attributes`
   */
  private static boolean hasWordPressLangAttribute(TagNode node) {
    return node.getAttributes().stream()
      .map(Attribute::getName)
      .anyMatch(attributeName -> attributeName.contains("?php") && attributeName.contains("language_attributes"));
  }

  /**
   * In Thymeleaf there are multiple ways of specifying the lang attribute:
   * - using the th:lang, th:xmllang, th:lang-xmllang attributes (lang-xmllang would set both xmllang and lang attributes)
   * - using the th:attr attribute for specifying different attributes. Example "th:attr="lang=html,xml:lang=html""
   */
  private static boolean hasThymeleafLangAttribute(TagNode node) {
    String thAttrValue = node.getAttribute("th:attr");
    return node.hasProperty("th:lang")
      || node.hasProperty("th:xmllang")
      || node.hasProperty("th:lang-xmllang")
      || (thAttrValue != null && thAttrValue.contains("lang=")
    );
  }

}
