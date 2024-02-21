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

import org.sonar.plugins.html.api.HtmlConstants;
import org.sonar.plugins.html.node.TagNode;

public class ControlGroup {
  public static boolean belongsToAutofillExpectationMantleControlGroup(TagNode node) {
    if (!node.getNodeName().equals(HtmlConstants.NAME_INPUT)) {
      return true;
    }

    String type = node.getAttribute("type");

    return type == null || !type.equals(HtmlConstants.TYPE_HIDDEN);
  }

  public static boolean belongsToDateControlGroup(TagNode node) {
    if (belongsToTextControlGroup(node)) {
      return true;
    }

    String type = node.getAttribute("type");

    return type != null && type.equals("date");
  }

  public static boolean belongsToMonthControlGroup(TagNode node) {
    String type = node.getAttribute("type");

    if (type != null && type.equals("month")) {
      return true;
    }

    return belongsToTextControlGroup(node);
  }

  public static boolean belongsToMultilineControlGroup(TagNode node) {
    if (node.getNodeName().equals(HtmlConstants.NAME_TEXTAREA) || node.getNodeName().equals("select")) {
      return true;
    }

    if (!node.getNodeName().equals(HtmlConstants.NAME_INPUT)) {
      return false;
    }

    String type = node.getAttribute("type");

    return type != null && type.equals(HtmlConstants.TYPE_HIDDEN);
  }

  public static boolean belongsToNumericControlGroup(TagNode node) {
    String type = node.getAttribute("type");

    if (type != null && type.equals("number")) {
      return true;
    }

    return belongsToTextControlGroup(node);
  }

  public static boolean belongsToPasswordControlGroup(TagNode node) {
    String type = node.getAttribute("type");

    if (type != null && type.equals("password")) {
      return true;
    }

    return belongsToTextControlGroup(node);
  }

  public static boolean belongsToTelControlGroup(TagNode node) {
    String type = node.getAttribute("type");

    if (type != null && type.equals("tel")) {
      return true;
    }

    return belongsToTextControlGroup(node);
  }

  public static boolean belongsToTextControlGroup(TagNode node) {
    if (node.getNodeName().equals(HtmlConstants.NAME_TEXTAREA) || node.getNodeName().equals("select")) {
      return true;
    }

    if (!node.getNodeName().equals(HtmlConstants.NAME_INPUT)) {
      return false;
    }

    String type = node.getAttribute("type");

    if (type == null) {
      return false;
    }

    return type.equals(HtmlConstants.TYPE_HIDDEN) || type.equals("text") || type.equals("search");
  }

  public static boolean belongsToUrlControlGroup(TagNode node) {
    String type = node.getAttribute("type");

    if (type != null && type.equals("url")) {
      return true;
    }

    return belongsToTextControlGroup(node);
  }

  public static boolean belongsToUsernameControlGroup(TagNode node) {
    String type = node.getAttribute("type");

    if (type != null && type.equals("email")) {
      return true;
    }

    return belongsToTextControlGroup(node);
  }

  private ControlGroup() {
  }
}
