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
package org.sonar.plugins.html.api.accessibility;

import org.sonar.plugins.html.node.TagNode;

public class ControlGroup {
  public static boolean belongsToAutofillExpectationMantleControlGroup(TagNode node) {
    if (!node.getNodeName().equalsIgnoreCase("input")) {
      return true;
    }

    var type = node.getAttribute("type");

    return type == null || !type.equalsIgnoreCase("hidden");
  }

  public static boolean belongsToDateControlGroup(TagNode node) {
    if (belongsToTextControlGroup(node)) {
      return true;
    }

    var type = node.getAttribute("type");

    return type != null && type.equalsIgnoreCase("date");
  }

  public static boolean belongsToMonthControlGroup(TagNode node) {
    var type = node.getAttribute("type");

    if (type != null && type.equalsIgnoreCase("month")) {
      return true;
    }

    return belongsToTextControlGroup(node);
  }

  public static boolean belongsToMultilineControlGroup(TagNode node) {
    var nodeName = node.getNodeName();

    if (nodeName.equalsIgnoreCase("textarea") || nodeName.equalsIgnoreCase("select")) {
      return true;
    }

    if (!nodeName.equalsIgnoreCase("input")) {
      return false;
    }

    var type = node.getAttribute("type");

    return type != null && type.equalsIgnoreCase("hidden");
  }

  public static boolean belongsToNumericControlGroup(TagNode node) {
    var type = node.getAttribute("type");

    if (type != null && type.equalsIgnoreCase("number")) {
      return true;
    }

    return belongsToTextControlGroup(node);
  }

  public static boolean belongsToPasswordControlGroup(TagNode node) {
    var type = node.getAttribute("type");

    if (type != null && type.equalsIgnoreCase("password")) {
      return true;
    }

    return belongsToTextControlGroup(node);
  }

  public static boolean belongsToTelControlGroup(TagNode node) {
    var type = node.getAttribute("type");

    if (type != null && type.equalsIgnoreCase("tel")) {
      return true;
    }

    return belongsToTextControlGroup(node);
  }

  public static boolean belongsToTextControlGroup(TagNode node) {
    var nodeName = node.getNodeName();

    if (nodeName.equalsIgnoreCase("textarea") || nodeName.equalsIgnoreCase("select")) {
      return true;
    }

    if (!nodeName.equalsIgnoreCase("input")) {
      return false;
    }

    var type = node.getAttribute("type");

    if (type == null) {
      return false;
    }

    return type.equalsIgnoreCase("hidden") || type.equalsIgnoreCase("text") || type.equalsIgnoreCase("search");
  }

  public static boolean belongsToUrlControlGroup(TagNode node) {
    var type = node.getAttribute("type");

    if (type != null && type.equalsIgnoreCase("url")) {
      return true;
    }

    return belongsToTextControlGroup(node);
  }

  public static boolean belongsToUsernameControlGroup(TagNode node) {
    var type = node.getAttribute("type");

    if (type != null && type.equalsIgnoreCase("email")) {
      return true;
    }

    return belongsToTextControlGroup(node);
  }

  private ControlGroup() {
  }
}
