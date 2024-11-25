/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (C) 2010-2024 SonarSource SA and Matthijs Galesloot
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package org.sonar.plugins.html.checks.sonar;

import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;

@Rule(key = "ImgWithoutAltCheck")
public class ImgWithoutAltCheck extends AbstractPageCheck {

  @Override
  public void startElement(TagNode node) {
    if ((isImgTag(node) && !hasAltAttribute(node) && !hasThymeleafAltAttribute(node)) ||
       ((isImageInput(node) || isAreaTag(node)) && hasInvalidAltAttribute(node))) {
      createViolation(node, "Add an \"alt\" attribute to this image.");
    }
  }

  private static boolean isImgTag(TagNode node) {
    return "IMG".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean isImageInput(TagNode node) {
    String type = node.getPropertyValue("TYPE");
    return "INPUT".equalsIgnoreCase(node.getNodeName()) && "IMAGE".equalsIgnoreCase(type);
  }

  private static boolean isAreaTag(TagNode node) {
    return "AREA".equalsIgnoreCase(node.getNodeName());
  }

  private static boolean hasAltAttribute(TagNode node) {
    return node.hasProperty("ALT");
  }

  private static boolean hasInvalidAltAttribute(TagNode node) {
    return !hasAltAttribute(node) || node.getPropertyValue("ALT").trim().isEmpty();
  }

  /**
   * In Thymeleaf there are multiple ways of specifying the alt for an img tag:
   * - using the th:alt or th:alt-title attributes (th:alt-title would set the title and alt to the same value)
   * - using the th:attr attribute for specifying different attributes. Example "th:attr="src=@{logo.png},title=#{logo},alt=#{logo}""
   */
  private static boolean hasThymeleafAltAttribute(TagNode node) {
    String thAttrValue = node.getAttribute("th:attr");
    return node.hasProperty("th:alt") || node.hasProperty("th:alt-title") ||
      (thAttrValue != null && thAttrValue.contains("alt="));
  }
}
