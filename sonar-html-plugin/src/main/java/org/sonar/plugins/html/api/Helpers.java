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
package org.sonar.plugins.html.api;

import org.sonar.plugins.html.node.TagNode;

public class Helpers {
  private Helpers() {
  }

  public static boolean isHeadingTag(TagNode node) {
    return node.getNodeName().length() == 2 &&
      Character.toUpperCase(node.getNodeName().charAt(0)) == 'H' &&
      node.getNodeName().charAt(1) >= '1' &&
      node.getNodeName().charAt(1) <= '6';
  }

  public static boolean isDynamicValue(String value) {
    return value.startsWith("<?php") || value.startsWith("{{") || value.startsWith("{%") || value.startsWith("<?=");
  }
}
