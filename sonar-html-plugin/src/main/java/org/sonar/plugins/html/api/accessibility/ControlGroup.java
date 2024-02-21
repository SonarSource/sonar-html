/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (c) 2010-2024 SonarSource SA and Matthijs Galesloot
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
