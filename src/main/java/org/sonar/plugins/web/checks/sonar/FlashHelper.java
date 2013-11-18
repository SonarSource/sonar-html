/*
 * SonarQube Web Plugin
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
package org.sonar.plugins.web.checks.sonar;

import org.sonar.plugins.web.node.TagNode;

import java.util.Locale;

public class FlashHelper {

  private FlashHelper() {
  }

  public static boolean isFlashObject(TagNode node) {
    return hasFlashClassId(node.getAttribute("classid")) ||
      hasFlashType(node.getAttribute("type")) ||
      hasFlashExtension(node.getAttribute("data"));
  }

  private static boolean hasFlashClassId(String classId) {
    return classId != null && "CLSID:D27CDB6E-AE6D-11CF-96B8-444553540000".equalsIgnoreCase(classId);
  }

  private static boolean hasFlashType(String type) {
    return type != null && type.toUpperCase(Locale.ENGLISH).contains("X-SHOCKWAVE-FLASH");
  }

  private static boolean hasFlashExtension(String file) {
    return file != null && file.toUpperCase(Locale.ENGLISH).endsWith(".SWF");
  }

  public static boolean isFlashEmbed(TagNode node) {
    return hasFlashType(node.getAttribute("type")) ||
      hasFlashExtension(node.getAttribute("src"));
  }

}
